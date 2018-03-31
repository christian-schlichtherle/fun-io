/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.diff;

import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.api.Source;
import global.namespace.fun.io.zip.io.*;
import global.namespace.fun.io.zip.model.DeltaModel;
import global.namespace.fun.io.zip.model.EntryNameAndDigest;
import global.namespace.fun.io.zip.model.EntryNameAndTwoDigests;

import javax.annotation.WillNotClose;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Optional.empty;

/**
 * Compares two archives entry by entry.
 * Archives may be ZIP, JAR, EAR or WAR files.
 *
 * @author Christian Schlichtherle
 */
public abstract class ZipDiff {

    /** Returns a new builder for a ZIP diff. */
    public static Builder builder() { return new Builder(); }

    public abstract void outputTo(ZipSink patch) throws Exception;

    /**
     * A builder for a ZIP diff.
     * The default message digest is SHA-1.
     */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
    public static class Builder {

        private Optional<String> digest = empty();
        private Optional<ZipSource> base = empty(), update = empty();

        Builder() { }

        public Builder digest(final String digest) {
            this.digest = Optional.of(digest);
            return this;
        }

        public Builder base(final ZipSource base) {
            this.base = Optional.of(base);
            return this;
        }

        public Builder update(final ZipSource update) {
            this.update = Optional.of(update);
            return this;
        }

        public ZipDiff build() { return create(digest, base.get(), update.get()); }

        private static ZipDiff create(final Optional<String> digestName, final ZipSource baseSource, final ZipSource updateSource) {
            return new ZipDiff() {

                @Override
                public void outputTo(final ZipSink patchSink) throws Exception {
                    baseSource.acceptZipReader(base ->
                            updateSource.acceptZipReader(update ->
                                    patchSink.acceptZipWriter(patch ->
                                            new Engine() {

                                                final MessageDigest digest =
                                                        MessageDigests.create(digestName.orElse("SHA-1"));

                                                protected MessageDigest digest() { return digest; }

                                                protected ZipInput base() { return base; }

                                                protected ZipInput update() { return update; }
                                            }.outputTo(patch)
                                    )
                            )
                    );
                }
            };
        }
    }

    /**
     * Compares two archives entry by entry.
     * Archives may be ZIP, JAR, EAR or WAR files.
     * This class requires you to implement its {@link MessageDigest} and {@link ZipInput} properties, but enables you
     * to obtain the delta {@linkplain #model model} besides {@linkplain #outputTo diffing} the input archives.
     *
     * @author Christian Schlichtherle
     */
    public abstract static class Engine {

        private static final Pattern COMPRESSED_FILE_EXTENSIONS =
                Pattern.compile(".*\\.(ear|jar|war|zip|gz|xz)", Pattern.CASE_INSENSITIVE);

        /** Returns the message digest. */
        protected abstract MessageDigest digest();

        /** Returns the first ZIP input file. */
        protected abstract @WillNotClose ZipInput base();

        /** Returns the second ZIP input file. */
        protected abstract @WillNotClose ZipInput update();

        /** Writes the ZIP patch file. */
        public void outputTo(final @WillNotClose ZipOutput patch) throws Exception {

            final class Streamer {

                final DeltaModel model = model();

                Streamer() throws Exception { model.encodeToXml(sink(entry(DeltaModel.ENTRY_NAME))); }

                Streamer stream() throws Exception {
                    for (final ZipEntry in : update()) {
                        final String name = in.getName();
                        if (changedOrAdded(name)) {
                            final ZipEntry out = entry(name);
                            if (COMPRESSED_FILE_EXTENSIONS.matcher(name).matches()) {
                                final long size = in.getSize();
                                out.setMethod(ZipOutputStream.STORED);
                                out.setSize(size);
                                out.setCompressedSize(size);
                                out.setCrc(in.getCrc());
                            }
                            Copy.copy(source2(in), sink(out));
                        }
                    }
                    return this;
                }

                Source source2(ZipEntry entry) { return new ZipEntrySource(entry, update()); }

                Sink sink(ZipEntry entry) { return new ZipEntrySink(entry, patch); }

                ZipEntry entry(String name) { return patch.entry(name); }

                boolean changedOrAdded(String name) { return null != model.changed(name) || null != model.added(name); }
            }

            new Streamer().stream();
        }

        /** Computes a delta model from the two input archives. */
        public DeltaModel model() throws Exception { return new Assembler().walkAndReturn(new Assembly()).deltaModel(); }

        private class Assembler {

            /**
             * Walks the given assembly through the two ZIP files and returns it.
             * If and only if the assembly throws an I/O exception, the assembler stops the visit and passes it on to
             * the caller.
             */
            Assembly walkAndReturn(final Assembly assembly) throws Exception {
                for (final ZipEntry entry1 : base()) {
                    if (entry1.isDirectory()) {
                        continue;
                    }
                    final Optional<ZipEntry> entry2 = update().entry(entry1.getName());
                    final ZipEntrySource source1 = new ZipEntrySource(entry1, base());
                    if (entry2.isPresent()) {
                        assembly.visitEntriesInBothFiles(source1, new ZipEntrySource(entry2.get(), update()));
                    } else {
                        assembly.visitEntryInFirstFile(source1);
                    }
                }

                for (final ZipEntry entry2 : update()) {
                    if (entry2.isDirectory()) {
                        continue;
                    }
                    final Optional<ZipEntry> entry1 = base().entry(entry2.getName());
                    if (!entry1.isPresent()) {
                        assembly.visitEntryInSecondFile(new ZipEntrySource(entry2, update()));
                    }
                }

                return assembly;
            }
        }

        /**
         * A visitor of two ZIP files.
         * Note that the order of the calls to the visitor methods is undefined, so you should not depend on the
         * behavior of the current implementation in order to ensure compatibility with future versions.
         */
        private class Assembly {

            private final Map<String, EntryNameAndTwoDigests> changed = new TreeMap<>();

            private final Map<String, EntryNameAndDigest>
                    unchanged = new TreeMap<>(),
                    added = new TreeMap<>(),
                    removed = new TreeMap<>();

            DeltaModel deltaModel() {
                return DeltaModel
                        .builder()
                        .messageDigest(digest())
                        .changedEntries(changed.values())
                        .unchangedEntries(unchanged.values())
                        .addedEntries(added.values())
                        .removedEntries(removed.values())
                        .build();
            }

            /**
             * Visits a pair of ZIP entries with equal names in the first and second ZIP file.
             *
             * @param source1 the ZIP entry in the first ZIP file.
             * @param source2 the ZIP entry in the second ZIP file.
             */
            void visitEntriesInBothFiles(final ZipEntrySource source1, final ZipEntrySource source2) throws Exception {
                final String name1 = source1.name();
                assert name1.equals(source2.name());
                final String digest1 = digestValueOf(source1);
                final String digest2 = digestValueOf(source2);
                if (digest1.equals(digest2)) {
                    unchanged.put(name1, new EntryNameAndDigest(name1, digest1));
                } else {
                    changed.put(name1, new EntryNameAndTwoDigests(name1, digest1, digest2));
                }
            }

            /**
             * Visits a ZIP entry which is present in the first ZIP file, but not in the second ZIP file.
             *
             * @param source1 the ZIP entry in the first ZIP file.
             */
            void visitEntryInFirstFile(final ZipEntrySource source1) throws Exception {
                final String name = source1.name();
                removed.put(name, new EntryNameAndDigest(name, digestValueOf(source1)));
            }

            /**
             * Visits a ZIP entry which is present in the second ZIP file, but not in the first ZIP file.
             *
             * @param source2 the ZIP entry in the second ZIP file.
             */
            void visitEntryInSecondFile(final ZipEntrySource source2) throws Exception {
                final String name = source2.name();
                added.put(name, new EntryNameAndDigest(name, digestValueOf(source2)));
            }

            String digestValueOf(Source source) throws Exception {
                final MessageDigest digest = digest();
                MessageDigests.updateDigestFrom(digest, source);
                return MessageDigests.valueOf(digest);
            }
        }
    }
}
