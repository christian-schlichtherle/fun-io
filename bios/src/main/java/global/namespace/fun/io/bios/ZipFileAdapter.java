/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveInput;
import global.namespace.fun.io.api.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static global.namespace.fun.io.api.ArchiveEntryNames.isInternal;
import static global.namespace.fun.io.api.ArchiveEntryNames.requireInternal;
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
            ZipEntry next;

            @Override
            public boolean hasNext() {
                if (null != next) {
                    return true;
                } else {
                    while (en.hasMoreElements()) {
                        final ZipEntry entry = en.nextElement();
                        if (isInternal(entry.getName())) {
                            next = entry;
                            return true;
                        }
                    }
                    return false;
                }
            }

            @Override
            public ArchiveEntrySource<ZipEntry> next() {
                if (hasNext()) {
                    final ZipEntry entry = next;
                    next = null;
                    return source(entry);
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    @Override
    public Optional<ArchiveEntrySource<ZipEntry>> source(String name) {
        return Optional.ofNullable(zip.getEntry(requireInternal(name))).map(this::source);
    }

    private ArchiveEntrySource<ZipEntry> source(ZipEntry entry) {
        return new ArchiveEntrySource<ZipEntry>() {

            @Override
            public String name() { return entry.getName(); }

            @Override
            public long size() { return entry.getSize(); }

            @Override
            public boolean isDirectory() { return entry.isDirectory(); }

            @Override
            public ZipEntry entry() { return entry; }

            @Override
            public Socket<InputStream> input() { return () -> zip.getInputStream(entry); }
        };
    }

    @Override
    public void close() throws IOException { zip.close(); }
}
