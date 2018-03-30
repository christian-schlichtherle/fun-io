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
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Compares two archives entry by entry.
 * Archives may be ZIP, JAR, EAR or WAR files.
 * This class requires you to implement its {@link ZipFile} and {@link MessageDigest} properties, but enables you to
 * obtain the delta {@linkplain #model model} besides {@linkplain #output diffing} the input archives.
 *
 * @author Christian Schlichtherle
 */
public abstract class ZipDiffEngine {

    private static final Pattern COMPRESSED_FILE_EXTENSIONS =
            Pattern.compile(".*\\.(ear|jar|war|zip|gz|xz)", Pattern.CASE_INSENSITIVE);

    /** Returns the message digest. */
    protected abstract MessageDigest digest();

    /** Returns the first input archive. */
    protected abstract @WillNotClose ZipInput input1();

    /** Returns the second input archive. */
    protected abstract @WillNotClose ZipInput input2();

    /** Writes the delta ZIP file. */
    public void output(final @WillNotClose ZipOutput delta) throws Exception {

        final class Streamer {

            final DeltaModel model = model();

            Streamer() throws Exception { model.encodeToXml(sink(entry(DeltaModel.ENTRY_NAME))); }

            Streamer stream() throws Exception {
                for (final ZipEntry in : input2()) {
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

            Source source2(ZipEntry entry) { return new ZipEntrySource(entry, input2()); }

            Sink sink(ZipEntry entry) { return new ZipEntrySink(entry, delta); }

            ZipEntry entry(String name) { return delta.entry(name); }

            boolean changedOrAdded(String name) { return null != model.changed(name) || null != model.added(name); }
        }

        new Streamer().stream();
    }

    /** Computes a delta model from the two input archives. */
    public DeltaModel model() throws Exception { return new Assembler().walkAndReturn(new Assembly()).deltaModel(); }

    private class Assembler {

        /**
         * Walks the given visitor through the two ZIP files and returns it.
         * If and only if the visitor throws an I/O exception, the assembler
         * stops the visit and passes it on to the caller.
         */
        <V extends Visitor> V walkAndReturn(final V visitor) throws Exception {
            for (final ZipEntry entry1 : input1()) {
                if (entry1.isDirectory()) {
                    continue;
                }
                final Optional<ZipEntry> entry2 = input2().entry(entry1.getName());
                final ZipEntrySource source1 = new ZipEntrySource(entry1, input1());
                if (entry2.isPresent()) {
                    visitor.visitEntriesInBothFiles(source1, new ZipEntrySource(entry2.get(), input2()));
                } else {
                    visitor.visitEntryInFirstFile(source1);
                }
            }

            for (final ZipEntry entry2 : input2()) {
                if (entry2.isDirectory()) {
                    continue;
                }
                final Optional<ZipEntry> entry1 = input1().entry(entry2.getName());
                if (!entry1.isPresent()) {
                    visitor.visitEntryInSecondFile(new ZipEntrySource(entry2, input2()));
                }
            }

            return visitor;
        }
    }

    private class Assembly implements Visitor {

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

        @Override
        public void visitEntriesInBothFiles(final ZipEntrySource source1, final ZipEntrySource source2)
        throws Exception {
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

        @Override
        public void visitEntryInFirstFile(final ZipEntrySource source1) throws Exception {
            final String name = source1.name();
            removed.put(name, new EntryNameAndDigest(name, digestValueOf(source1)));
        }

        @Override
        public void visitEntryInSecondFile(final ZipEntrySource source2) throws Exception {
            final String name = source2.name();
            added.put(name, new EntryNameAndDigest(name, digestValueOf(source2)));
        }

        String digestValueOf(Source source) throws Exception {
            final MessageDigest digest = digest();
            digest.reset();
            MessageDigests.updateDigestFrom(digest, source);
            return MessageDigests.valueOf(digest);
        }
    }

    /**
     * A visitor of two ZIP files.
     * Note that the order of the calls to the visitor methods is undefined,
     * so you should not depend on the behavior of the current implementation
     * in order to ensure compatibility with future versions.
     */
    private interface Visitor {

        /**
         * Visits a ZIP entry which is present in the first ZIP file,
         * but not in the second ZIP file.
         *
         * @param source1 the ZIP entry in the first ZIP file.
         */
        void visitEntryInFirstFile(ZipEntrySource source1) throws Exception;

        /**
         * Visits a ZIP entry which is present in the second ZIP file,
         * but not in the first ZIP file.
         *
         * @param source2 the ZIP entry in the second ZIP file.
         */
        void visitEntryInSecondFile(ZipEntrySource source2) throws Exception;

        /**
         * Visits a pair of ZIP entries with equal names in the first and
         * second ZIP file.
         *
         * @param source1 the ZIP entry in the first ZIP file.
         * @param source2 the ZIP entry in the second ZIP file.
         */
        void visitEntriesInBothFiles(ZipEntrySource source1, ZipEntrySource source2) throws Exception;
    }
}
