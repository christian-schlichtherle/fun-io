/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveInputStream;
import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.spi.UncloseableInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import static global.namespace.fun.io.spi.ArchiveEntryNames.isInternal;

/**
 * Adapts a {@link TarArchiveInputStream} to an {@link ArchiveInputStream}.
 *
 * @author Christian Schlichtherle
 */
final class TarArchiveInputStreamAdapter implements ArchiveInputStream {

    private final TarArchiveInputStream tar;

    TarArchiveInputStreamAdapter(final TarArchiveInputStream tar) { this.tar = tar; }

    public Iterator<ArchiveEntrySource> iterator() {
        return new Iterator<ArchiveEntrySource>() {

            Object next;

            @Override
            public boolean hasNext() {
                if (null != next) {
                    return true;
                } else {
                    try {
                        TarArchiveEntry entry;
                        while (null != (entry = tar.getNextTarEntry())) {
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
            public ArchiveEntrySource next() {
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

    public Optional<ArchiveEntrySource> source(String name) {
        throw new UnsupportedOperationException();
    }

    private ArchiveEntrySource source(TarArchiveEntry entry) {
        return new ArchiveEntrySource() {

            @Override
            public Socket<InputStream> input() {
                return () -> {
                    if (entry != tar.getCurrentEntry()) {
                        throw new IllegalStateException("The TAR input stream is currently reading a different entry.");
                    }
                    return new UncloseableInputStream(tar);
                };
            }

            @Override
            public String name() { return entry.getName(); }

            @Override
            public boolean directory() { return entry.isDirectory(); }

            @Override
            public long size() { return entry.getSize(); }
        };
    }

    @Override
    public void close() throws IOException { tar.close(); }
}
