/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveInput;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import static global.namespace.fun.io.bios.BIOS.copy;
import static global.namespace.fun.io.bios.BIOS.stream;
import static java.util.Objects.requireNonNull;

/**
 * Adapts a {@link TarArchiveInputStream} to an {@link ArchiveInput}.
 *
 * @author Christian Schlichtherle
 */
final class TarArchiveInputStreamAdapter implements ArchiveInput<TarArchiveEntry> {

    private final TarArchiveInputStream tar;

    TarArchiveInputStreamAdapter(final TarArchiveInputStream input) { this.tar = requireNonNull(input); }

    public Iterator<ArchiveEntrySource<TarArchiveEntry>> iterator() {
        return new Iterator<ArchiveEntrySource<TarArchiveEntry>>() {

            Object next;

            public boolean hasNext() {
                if (null == next) {
                    try {
                        next = tar.getNextTarEntry();
                    } catch (IOException e) {
                        next = e;
                    }
                }
                return null != next;
            }

            public ArchiveEntrySource<TarArchiveEntry> next() {
                if (hasNext()) {
                    if (next instanceof TarArchiveEntry) {
                        final TarArchiveEntry entry = (TarArchiveEntry) next;
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

    public Optional<ArchiveEntrySource<TarArchiveEntry>> source(String name) {
        throw new UnsupportedOperationException();
    }

    private ArchiveEntrySource<TarArchiveEntry> source(TarArchiveEntry entry) {
        return new ArchiveEntrySource<TarArchiveEntry>() {

            public String name() { return entry.getName(); }

            public boolean isDirectory() { return entry.isDirectory(); }

            public TarArchiveEntry entry() { return entry; }

            public Socket<InputStream> input() {
                return stream(tar).input().map(in -> {
                    if (entry != tar.getCurrentEntry()) {
                        throw new IllegalStateException("The TAR input stream is currently reading a different entry.");
                    }
                    return in;
                });
            }

            public void copyTo(ArchiveEntrySink<?> sink) throws Exception { copy(this, sink); }
        };
    }

    @Override
    public void close() throws IOException { tar.close(); }
}
