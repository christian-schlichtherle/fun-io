/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Socket;

import javax.annotation.Nullable;
import javax.annotation.WillCloseWhenClosed;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.Objects.requireNonNull;

/**
 * Adapts a {@link ZipFile} to a {@link ZipInput}.
 *
 * @see ZipOutputStreamAdapter
 * @author Christian Schlichtherle
 */
public class ZipFileAdapter implements ZipInput {

    private final ZipFile zip;

    /** Constructs a new ZIP file adapter for the given ZIP file. */
    public ZipFileAdapter(final @WillCloseWhenClosed ZipFile input) { this.zip = requireNonNull(input); }

    @Override
    public Iterator<ZipEntry> iterator() {
        return new Iterator<ZipEntry>() {

            final Enumeration<? extends ZipEntry> en = zip.entries();

            @Override public boolean hasNext() { return en.hasMoreElements(); }

            @Override public ZipEntry next() { return en.nextElement(); }

            @Override public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Optional<ZipEntry> entry(String name) { return Optional.ofNullable(zip.getEntry(name)); }

    @Override
    public Socket<InputStream> input(ZipEntry entry) { return () -> zip.getInputStream(entry); }

    @Override
    public void close() throws IOException { zip.close(); }
}
