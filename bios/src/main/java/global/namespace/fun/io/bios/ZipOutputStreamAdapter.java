/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveOutput;
import global.namespace.fun.io.api.Socket;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;
import static global.namespace.fun.io.spi.Copy.copy;
import static java.util.Objects.requireNonNull;

/**
 * Adapts a {@link ZipOutputStream} to an {@link ArchiveOutput}.
 *
 * @author Christian Schlichtherle
 */
class ZipOutputStreamAdapter implements ArchiveOutput<ZipEntry> {

    private final ZipOutputStream zip;

    ZipOutputStreamAdapter(final ZipOutputStream zip) { this.zip = requireNonNull(zip); }

    @Override
    public ArchiveEntrySink<ZipEntry> sink(String name) { return sink(new ZipEntry(requireInternal(name))); }

    ArchiveEntrySink<ZipEntry> sink(ZipEntry entry) {
        return new ArchiveEntrySink<ZipEntry>() {

            @Override
            public String name() { return entry.getName(); }

            @Override
            public long size() { return entry.getSize(); }

            @Override
            public boolean isDirectory() { return entry.isDirectory(); }

            @Override
            public ZipEntry entry() { return entry; }

            @Override
            public Socket<OutputStream> output() {
                return () -> {
                    if (entry.isDirectory()) {
                        entry.setMethod(ZipOutputStream.STORED);
                        entry.setSize(0);
                        entry.setCompressedSize(0);
                        entry.setCrc(0);
                    }
                    zip.putNextEntry(entry);
                    return new FilterOutputStream(zip) {

                        @Override
                        public void close() throws IOException { zip.closeEntry(); }
                    };
                };
            }

            @Override
            public void copyFrom(ArchiveEntrySource<?> source) throws Exception { copy(source, this); }
        };
    }

    @Override
    public void close() throws IOException { zip.close(); }
}
