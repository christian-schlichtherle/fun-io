/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveOutput;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static global.namespace.fun.io.bios.BIOS.copy;

/**
 * Adapts a {@link TarArchiveOutputStream} to an {@link ArchiveOutput}.
 *
 * @author Christian Schlichtherle
 */
final class TarArchiveOutputStreamAdapter implements ArchiveOutput<TarArchiveEntry> {

    private final TarArchiveOutputStream tar;

    TarArchiveOutputStreamAdapter(final TarArchiveOutputStream tar) { this.tar = tar; }

    /** Returns {@code false}. */
    public boolean isJar() { return false; }

    public ArchiveEntrySink<TarArchiveEntry> sink(String name) { return sink(new TarArchiveEntry(name)); }

    private ArchiveEntrySink<TarArchiveEntry> sink(TarArchiveEntry entry) {
        return new ArchiveEntrySink<TarArchiveEntry>() {

            @Override
            public String name() { return entry.getName(); }

            @Override
            public long size() { return entry.getSize(); }

            @Override
            public boolean isDirectory() { return entry.isDirectory(); }

            @Override
            public TarArchiveEntry entry() { return entry; }

            @Override
            public Socket<OutputStream> output() {
                return () -> {
                    tar.putArchiveEntry(entry);
                    return new FilterOutputStream(tar) {

                        boolean closed;

                        @Override
                        public void close() throws IOException {
                            if (!closed) {
                                closed = true;
                                tar.closeArchiveEntry(); // not idempotent!
                            }
                        }
                    };
                };
            }

            @Override
            public void copyFrom(ArchiveEntrySource<?> source) throws Exception {
                entry.setSize(source.size());
                copy(source, this);
            }
        };
    }

    @Override
    public void close() throws IOException { tar.close(); }
}
