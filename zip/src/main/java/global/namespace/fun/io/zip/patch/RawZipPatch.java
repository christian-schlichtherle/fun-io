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
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Applies a delta ZIP file to an input archive and generates an output archive.
 * Archives may be ZIP, JAR, EAR or WAR files.
 * This class requires you to implement its {@link ZipFile} properties.
 *
 * @author Christian Schlichtherle
 */
@ThreadSafe
public abstract class RawZipPatch {

    private volatile DeltaModel model;

    /** Returns the input archive. */
    protected abstract @WillNotClose
    ZipInput input();

    /** Returns the delta ZIP archive. */
    protected abstract @WillNotClose ZipInput delta();

    /**
     * Applies the configured delta ZIP archive.
     */
    public void output(final @WillNotClose ZipOutput output) throws Exception {
        for (EntryNameFilter filter : passFilters(output)) {
            output(output, new NoDirectoryEntryNameFilter(filter));
        }
    }

    /**
     * Returns a list of filters for the different passes required to process
     * the output ZIP file.
     * At least one filter is required to output anything.
     * The filters should properly partition the set of entry sources,
     * i.e. each entry source should be accepted by exactly one filter.
     */
    private EntryNameFilter[] passFilters(final @WillNotClose ZipOutput output) {
        if (output.entry("") instanceof JarEntry) {
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
            return new EntryNameFilter[] {
                    manifestFilter,
                    new InverseEntryNameFilter(manifestFilter)
            };
        } else {
            return new EntryNameFilter[] { new AcceptAllEntryNameFilter() };
        }
    }

    private void output(final @WillNotClose ZipOutput output, final EntryNameFilter filter) throws Exception {

        class ZipEntrySink implements Sink {

            final EntryNameAndDigest entryNameAndDigest;

            ZipEntrySink(final EntryNameAndDigest entryNameAndDigest) {
                assert null != entryNameAndDigest;
                this.entryNameAndDigest = entryNameAndDigest;
            }

            @Override
            public Socket<OutputStream> output() {
                final ZipEntry entry = entry(entryNameAndDigest.name());
                return output(entry).map(out -> {
                    final MessageDigest digest = digest();
                    digest.reset();
                    return new DigestOutputStream(out, digest) {

                        @Override
                        public void close() throws IOException {
                            super.close();
                            if (!valueOfDigest().equals(entryNameAndDigest.digest())) {
                                throw new WrongMessageDigestException(entryNameAndDigest.name());
                            }
                        }

                        String valueOfDigest() { return MessageDigests.valueOf(digest); }
                    };
                });
            }

            ZipEntry entry(String name) { return output.entry(name); }

            Socket<OutputStream> output(ZipEntry entry) { return output.output(entry); }
        }

        abstract class PatchSet {

            abstract ZipInput archive();

            abstract IOException ioException(Throwable cause);

            final <T> PatchSet apply(final Transformation<T> transformation, final Iterable<T> iterable)
            throws Exception {
                for (final T item : iterable) {
                    final EntryNameAndDigest
                            entryNameAndDigest = transformation.apply(item);
                    final String name = entryNameAndDigest.name();
                    if (!filter.accept(name)) continue;
                    final ZipEntry entry = archive().entry(name);
                    if (null == entry) {
                        throw ioException(new MissingZipEntryException(name));
                    }
                    try {
                        Copy.copy(new ZipEntrySource(entry, archive()), new ZipEntrySink(entryNameAndDigest));
                    } catch (WrongMessageDigestException ex) {
                        throw ioException(ex);
                    }
                }
                return this;
            }
        }

        class InputArchivePatchSet extends PatchSet {

            @Override ZipInput archive() { return input(); }

            @Override IOException ioException(Throwable cause) {
                return new WrongInputZipFile(cause);
            }
        }

        class PatchArchivePatchSet extends PatchSet {

            @Override ZipInput archive() { return delta(); }

            @Override IOException ioException(Throwable cause) {
                return new InvalidDiffZipFileException(cause);
            }
        }

        // Order is important here!
        new InputArchivePatchSet().apply(
                new IdentityTransformation(),
                model().unchangedEntries());
        new PatchArchivePatchSet().apply(
                new EntryNameAndDigest2Transformation(),
                model().changedEntries());
        new PatchArchivePatchSet().apply(
                new IdentityTransformation(),
                model().addedEntries());
    }

    private MessageDigest digest() throws Exception {
        return MessageDigests.create(model().digestAlgorithmName());
    }

    private DeltaModel model() throws Exception {
        final DeltaModel model = this.model;
        return null != model ? model : (this.model = loadModel());
    }

    private DeltaModel loadModel() throws Exception {
        return DeltaModel.decodeFromXml(new ZipEntrySource(modelZipEntry(), delta()));
    }

    private ZipEntry modelZipEntry() throws Exception {
        final String name = DeltaModel.ENTRY_NAME;
        final ZipEntry entry = delta().entry(name);
        if (null == entry) {
            throw new InvalidDiffZipFileException(new MissingZipEntryException(name));
        }
        return entry;
    }
}
