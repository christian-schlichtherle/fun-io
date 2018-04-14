/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveInput;
import global.namespace.fun.io.api.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static global.namespace.fun.io.bios.BIOS.copy;
import static java.util.Objects.requireNonNull;

/**
 * Adapts a {@link ZipFile} to an {@link ArchiveInput}.
 *
 * @author Christian Schlichtherle
 */
final class ZipFileAdapter implements ArchiveInput<ZipEntry> {

    private final ZipFile zip;

    ZipFileAdapter(final ZipFile input) { this.zip = requireNonNull(input); }

    @Override
    public Iterator<ArchiveEntrySource<ZipEntry>> iterator() {
        return new Iterator<ArchiveEntrySource<ZipEntry>>() {

            final Enumeration<? extends ZipEntry> en = zip.entries();

            public boolean hasNext() { return en.hasMoreElements(); }

            public ArchiveEntrySource<ZipEntry> next() { return source(en.nextElement()); }
        };
    }

    @Override
    public Optional<ArchiveEntrySource<ZipEntry>> source(String name) {
        return Optional.ofNullable(zip.getEntry(name)).map(this::source);
    }

    private ArchiveEntrySource<ZipEntry> source(ZipEntry entry) {
        return new ArchiveEntrySource<ZipEntry>() {

            public String name() { return entry.getName(); }

            public boolean isDirectory() { return entry.isDirectory(); }

            public ZipEntry entry() { return entry; }

            public Socket<InputStream> input() { return () -> zip.getInputStream(entry); }

            public void copyTo(ArchiveEntrySink<?> sink) throws Exception { copy(this, sink); }
        };
    }

    @Override
    public void close() throws IOException { zip.close(); }
}
