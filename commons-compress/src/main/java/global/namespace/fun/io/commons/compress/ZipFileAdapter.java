/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveInput;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Optional;

import static global.namespace.fun.io.bios.BIOS.copy;
import static java.util.Objects.requireNonNull;

/**
 * Adapts a {@link ZipFile} to an {@link ArchiveInput}.
 *
 * @author Christian Schlichtherle
 */
final class ZipFileAdapter implements ArchiveInput<ZipArchiveEntry> {

    private final ZipFile zip;

    ZipFileAdapter(final ZipFile input) { this.zip = requireNonNull(input); }

    public Iterator<ArchiveEntrySource<ZipArchiveEntry>> iterator() {
        return new Iterator<ArchiveEntrySource<ZipArchiveEntry>>() {

            final Enumeration<ZipArchiveEntry> en = zip.getEntries();

            public boolean hasNext() { return en.hasMoreElements(); }

            public ArchiveEntrySource<ZipArchiveEntry> next() { return source(en.nextElement()); }
        };
    }

    public Optional<ArchiveEntrySource<ZipArchiveEntry>> source(String name) {
        return Optional.ofNullable(zip.getEntry(name)).map(this::source);
    }

    private ZipArchiveEntrySource source(ZipArchiveEntry entry) {
        return new ZipArchiveEntrySource() {

            public String name() { return entry.getName(); }

            public boolean isDirectory() { return entry.isDirectory(); }

            public ZipArchiveEntry entry() { return entry; }

            public Socket<InputStream> input() { return () -> zip.getInputStream(entry); }

            Socket<InputStream> rawInput() { return () -> zip.getRawInputStream(entry); }

            public void copyTo(final ArchiveEntrySink<?> sink) throws Exception {
                if (sink instanceof ZipArchiveEntrySink) {
                    copyTo((ZipArchiveEntrySink) sink);
                } else {
                    copy(this, sink);
                }
            }

            void copyTo(ZipArchiveEntrySink sink) throws Exception {
                sink.copyFrom(this);
            }
        };
    }

    @Override
    public void close() throws IOException { zip.close(); }
}
