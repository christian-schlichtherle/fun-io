/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveOutput;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static global.namespace.fun.io.bios.BIOS.copy;
import static java.util.Objects.requireNonNull;

/**
 * Adapts a {@link ZipArchiveOutputStream} to an {@link ArchiveOutput}.
 *
 * @author Christian Schlichtherle
 */
class ZipArchiveOutputStreamAdapter implements ArchiveOutput<ZipArchiveEntry> {

    private final ZipArchiveOutputStream zip;

    ZipArchiveOutputStreamAdapter(final ZipArchiveOutputStream zip) { this.zip = requireNonNull(zip); }

    /** Returns {@code false}. */
    public boolean isJar() { return false; }

    public ArchiveEntrySink<ZipArchiveEntry> sink(String name) { return sink(new ZipArchiveEntry(name)); }

    ZipArchiveEntrySink sink(ZipArchiveEntry entry) {
        return new ZipArchiveEntrySink() {

            public String name() { return entry.getName(); }

            public boolean isDirectory() { return entry.isDirectory(); }

            public ZipArchiveEntry entry() { return entry; }

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
                                ((ArchiveOutputStream) out).closeArchiveEntry(); // not idempotent!
                            }
                        }
                    };
                };
            }

            void copyFrom(ZipArchiveEntrySource source) throws Exception {
                final ZipArchiveEntry origin = source.entry();
                if (origin.getName().equals(entry.getName())) {
                    source.rawInput().accept(in -> zip.addRawArchiveEntry(origin, in));
                } else {
                    copy(source, this);
                }
            }
        };
    }

    @Override
    public void close() throws IOException { zip.close(); }
}
