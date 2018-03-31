/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.patch;

import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.zip.io.*;
import global.namespace.fun.io.zip.model.DeltaModel;
import global.namespace.fun.io.zip.model.EntryNameAndDigest;

import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

import static java.util.Optional.empty;

/**
 * Applies a delta ZIP file to an input archive and generates an output archive.
 * Archives may be ZIP, JAR, EAR or WAR files.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public abstract class ZipPatch {

    /** Returns a new builder for a ZIP patch. */
    public static Builder builder() { return new Builder(); }

    public abstract void outputTo(ZipSink update) throws Exception;

    /** A builder for a ZIP patch. */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
    public static class Builder {

        private Optional<ZipSource> base = empty(), patch = empty();

        Builder() { }

        public Builder base(final ZipSource base) {
            this.base = Optional.of(base);
            return this;
        }

        public Builder patch(final ZipSource patch) {
            this.patch = Optional.of(patch);
            return this;
        }

        public ZipPatch build() { return create(base.get(), patch.get()); }

        private static ZipPatch create(final ZipSource baseSource, final ZipSource patchSource) {
            return new ZipPatch() {

                @Override
                public void outputTo(final ZipSink updateSink) throws Exception {
                    baseSource.acceptZipReader(base ->
                            patchSource.acceptZipReader(patch ->
                                    updateSink.acceptZipWriter(update ->
                                            new ZipPatchEngine() {

                                                protected ZipInput base() { return base; }

                                                protected ZipInput patch() { return patch; }
                                            }.outputTo(update)
                                    )
                            )
                    );
                }
            };
        }
    }

    /**
     * Applies a delta ZIP file to a base ZIP file and generates an update ZIP file.
     * Archives may be ZIP, JAR, EAR or WAR files.
     * This class requires you to implement its {@link ZipInput} properties.
     *
     * @author Christian Schlichtherle
     */
    public abstract static class ZipPatchEngine {

        private volatile DeltaModel model;

        /** Returns the base ZIP file. */
        protected abstract @WillNotClose ZipInput base();

        /** Returns the patch ZIP file. */
        protected abstract @WillNotClose ZipInput patch();

        /** Writes the output to the given update ZIP file. */
        public void outputTo(final @WillNotClose ZipOutput update) throws Exception {
            for (EntryNameFilter filter : passFilters(update)) {
                outputTo(update, new NoDirectoryEntryNameFilter(filter));
            }
        }

        /**
         * Returns a list of filters for the different passes required to process the update ZIP file.
         * At least one filter is required to output anything.
         * The filters should properly partition the set of entry sources, i.e. each entry source should be accepted by
         * exactly one filter.
         */
        private EntryNameFilter[] passFilters(final @WillNotClose ZipOutput update) {
            if (update.entry("") instanceof JarEntry) {
                // The JarInputStream class assumes that the file entry
                // "META-INF/MANIFEST.MF" should either be the first or the second
                // entry (if preceded by the directory entry "META-INF/"), so we
                // need to process the delta ZIP file in two passes with a
                // corresponding filter to ensure this order.
                // Note that the directory entry "META-INF/" is always part of the
                // unchanged patch set because it's content is always empty.
                // Thus, by copying the unchanged entries before the changed
                // entries, the directory entry "META-INF/" will always appear
                // before the file entry "META-INF/MANIFEST.MF".
                final EntryNameFilter manifestFilter = new ManifestEntryNameFilter();
                return new EntryNameFilter[] { manifestFilter, new InverseEntryNameFilter(manifestFilter) };
            } else {
                return new EntryNameFilter[] { new AcceptAllEntryNameFilter() };
            }
        }

        private void outputTo(final @WillNotClose ZipOutput update, final EntryNameFilter filter) throws Exception {

            class ZipEntrySink implements Sink {

                final EntryNameAndDigest entryNameAndDigest;

                ZipEntrySink(final EntryNameAndDigest entryNameAndDigest) {
                    assert null != entryNameAndDigest;
                    this.entryNameAndDigest = entryNameAndDigest;
                }

                @Override
                public Socket<OutputStream> output() {
                    final ZipEntry entry = entry(entryNameAndDigest.name());
                    return output(entry).map(out ->
                        new DigestOutputStream(out, digest()) {

                            @Override
                            public void close() throws IOException {
                                super.close();
                                if (!valueOfDigest().equals(entryNameAndDigest.digest())) {
                                    throw new WrongMessageDigestException(entryNameAndDigest.name());
                                }
                            }

                            String valueOfDigest() { return MessageDigests.valueOf(digest); }
                        }
                    );
                }

                ZipEntry entry(String name) { return update.entry(name); }

                Socket<OutputStream> output(ZipEntry entry) { return update.output(entry); }
            }

            abstract class PatchSet {

                abstract ZipInput input();

                abstract IOException ioException(Throwable cause);

                final <T> void apply(final Transformation<T> transformation, final Iterable<T> iterable) throws Exception {
                    for (final T item : iterable) {
                        final EntryNameAndDigest entryNameAndDigest = transformation.apply(item);
                        final String name = entryNameAndDigest.name();
                        if (!filter.accept(name)) {
                            continue;
                        }
                        final Optional<ZipEntry> entry = input().entry(name);
                        try {
                            Copy.copy(
                                    new ZipEntrySource(entry.orElseThrow(() -> ioException(new MissingZipEntryException(name))), input()),
                                    new ZipEntrySink(entryNameAndDigest)
                            );
                        } catch (WrongMessageDigestException e) {
                            throw ioException(e);
                        }
                    }
                }
            }

            class BaseArchivePatchSet extends PatchSet {

                @Override
                ZipInput input() { return base(); }

                @Override
                IOException ioException(Throwable cause) { return new WrongBaseZipFileException(cause); }
            }

            class PatchArchivePatchSet extends PatchSet {

                @Override
                ZipInput input() { return patch(); }

                @Override
                IOException ioException(Throwable cause) { return new InvalidPatchZipFileException(cause); }
            }

            // Order is important here!
            new BaseArchivePatchSet().apply(new IdentityTransformation(), model().unchangedEntries());
            new PatchArchivePatchSet().apply(new EntryNameAndDigest2Transformation(), model().changedEntries());
            new PatchArchivePatchSet().apply(new IdentityTransformation(), model().addedEntries());
        }

        private MessageDigest digest() throws Exception {
            return MessageDigests.create(model().digestAlgorithmName());
        }

        private DeltaModel model() throws Exception {
            final DeltaModel model = this.model;
            return null != model ? model : (this.model = loadModel());
        }

        private DeltaModel loadModel() throws Exception {
            return DeltaModel.decodeFromXml(new ZipEntrySource(modelZipEntry(), patch()));
        }

        private ZipEntry modelZipEntry() throws Exception {
            final String name = DeltaModel.ENTRY_NAME;
            return patch().entry(name).orElseThrow(() -> new InvalidPatchZipFileException(new MissingZipEntryException(name)));
        }
    }
}
