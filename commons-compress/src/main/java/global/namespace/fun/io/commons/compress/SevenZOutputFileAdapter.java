/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveOutput;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.IOException;
import java.io.OutputStream;

import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;
import static global.namespace.fun.io.spi.Copy.copy;

/**
 * Adapts a {@link SevenZOutputFile} to an {@link ArchiveOutput}.
 *
 * @author Christian Schlichtherle
 */
final class SevenZOutputFileAdapter implements ArchiveOutput<SevenZArchiveEntry> {

    private final SevenZOutputFile sevenz;

    SevenZOutputFileAdapter(final SevenZOutputFile sevenz) { this.sevenz = sevenz; }

    public ArchiveEntrySink<SevenZArchiveEntry> sink(String name) {
        final SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setName(requireInternal(name));
        return sink(entry);
    }

    private ArchiveEntrySink<SevenZArchiveEntry> sink(SevenZArchiveEntry entry) {
        return new ArchiveEntrySink<SevenZArchiveEntry>() {

            @Override
            public String name() { return entry.getName(); }

            @Override
            public long size() { return entry.getSize(); }

            @Override
            public boolean isDirectory() { return entry.isDirectory(); }

            @Override
            public SevenZArchiveEntry entry() { return entry; }

            @Override
            public Socket<OutputStream> output() {
                return () -> {
                    sevenz.putArchiveEntry(entry);
                    return new OutputStream() {

                        boolean closed;

                        @Override
                        public void write(int b) throws IOException { sevenz.write(b); }

                        @Override
                        public void write(byte[] b) throws IOException { sevenz.write(b); }

                        @Override
                        public void write(byte[] b, int off, int len) throws IOException { sevenz.write(b, off, len); }

                        @Override
                        public void flush() { }

                        @Override
                        public void close() throws IOException {
                            if (!closed) {
                                closed = true;
                                sevenz.closeArchiveEntry(); // not idempotent!
                            }
                        }
                    };
                };
            }

            @Override
            public void copyFrom(ArchiveEntrySource<?> source) throws Exception {
                entry.setSize(source.size());
                entry.setDirectory(source.isDirectory());
                copy(source, this);
            }
        };
    }

    @Override
    public void close() throws IOException { sevenz.close(); }
}
