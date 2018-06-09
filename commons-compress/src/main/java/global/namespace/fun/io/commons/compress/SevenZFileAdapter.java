/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveInput;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import static global.namespace.fun.io.bios.ArchiveEntryNames.isInternal;

/**
 * Adapts a {@link TarArchiveInputStream} to an {@link ArchiveInput}.
 *
 * @author Christian Schlichtherle
 */
final class SevenZFileAdapter implements ArchiveInput<SevenZArchiveEntry> {

    private final SevenZFile sevenz;

    SevenZFileAdapter(final SevenZFile sevenz) { this.sevenz = sevenz; }

    public Iterator<ArchiveEntrySource<SevenZArchiveEntry>> iterator() {
        return new Iterator<ArchiveEntrySource<SevenZArchiveEntry>>() {

            Object next;

            @Override
            public boolean hasNext() {
                if (null != next) {
                    return true;
                } else {
                    try {
                        SevenZArchiveEntry entry;
                        while (null != (entry = sevenz.getNextEntry())) {
                            if (isInternal(entry.getName())) {
                                next = entry;
                                return true;
                            }
                        }
                    } catch (IOException e) {
                        next = e;
                        return true;
                    }
                    return false;
                }
            }

            @Override
            public ArchiveEntrySource<SevenZArchiveEntry> next() {
                if (hasNext()) {
                    if (next instanceof SevenZArchiveEntry) {
                        final SevenZArchiveEntry entry = (SevenZArchiveEntry) next;
                        next = null;
                        return source(entry);
                    } else if (next instanceof Exception) {
                        throw (NoSuchElementException) new NoSuchElementException(next.toString()).initCause((Exception) next);
                    }
                }
                throw new NoSuchElementException();
            }
        };
    }

    public Optional<ArchiveEntrySource<SevenZArchiveEntry>> source(String name) {
        throw new UnsupportedOperationException();
    }

    private ArchiveEntrySource<SevenZArchiveEntry> source(SevenZArchiveEntry entry) {
        return new ArchiveEntrySource<SevenZArchiveEntry>() {

            @Override
            public String name() { return entry.getName(); }

            @Override
            public long size() { return entry.getSize(); }

            @Override
            public boolean isDirectory() { return entry.isDirectory(); }

            @Override
            public SevenZArchiveEntry entry() { return entry; }

            @Override
            public Socket<InputStream> input() {
                return () -> new InputStream() {

                    @Override
                    public int read() throws IOException { return sevenz.read(); }

                    @Override
                    public int read(byte[] b) throws IOException { return sevenz.read(b); }

                    @Override
                    public int read(byte[] b, int off, int len) throws IOException { return sevenz.read(b, off, len); }
                };
            }
        };
    }

    @Override
    public void close() throws IOException { sevenz.close(); }
}
