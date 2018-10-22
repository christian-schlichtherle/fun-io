/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveOutput;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;
import static global.namespace.fun.io.spi.Copy.copy;

/**
 * Adapts a {@link ZipArchiveOutputStream} to an {@link ArchiveOutput}.
 *
 * @author Christian Schlichtherle
 */
class ZipArchiveOutputStreamAdapter implements ArchiveOutput {

    private final ZipArchiveOutputStream zip;

    ZipArchiveOutputStreamAdapter(final ZipArchiveOutputStream zip) {
        this.zip = zip;
    }

    @Override
    public ArchiveEntrySink sink(String name) {
        return sink(new ZipArchiveEntry(requireInternal(name)));
    }

    ArchiveEntrySink sink(ZipArchiveEntry entry) {
        return new ArchiveEntrySink() {

            @Override
            public Socket<OutputStream> output() {
                return () -> {
                    if (entry.isDirectory()) {
                        entry.setMethod(ZipArchiveOutputStream.STORED);
                        entry.setSize(0);
                        entry.setCompressedSize(0);
                        entry.setCrc(0);
                    }
                    zip.putArchiveEntry(entry);
                    return new FilterOutputStream(zip) {

                        boolean closed;

                        @Override
                        public void close() throws IOException {
                            if (!closed) {
                                closed = true;
                                zip.closeArchiveEntry(); // not idempotent!
                            }
                        }
                    };
                };
            }

            @Override
            public void copyFrom(final ArchiveEntrySource source) throws Exception {
                if (source instanceof ZipArchiveEntrySource) {
                    final ZipArchiveEntrySource zipSource = (ZipArchiveEntrySource) source;
                    final ZipArchiveEntry origin = zipSource.entry();
                    if (origin.getName().equals(entry.getName())) {
                        zipSource.rawInput().accept(in -> zip.addRawArchiveEntry(origin, in));
                        return;
                    }
                }
                copy(source, this);
            }
        };
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }
}
