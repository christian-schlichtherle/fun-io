/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.io;

import javax.annotation.Nullable;
import javax.annotation.WillCloseWhenClosed;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
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

    /** The adapted ZIP file. */
    protected ZipFile zip;

    /** Use of this constructor requires setting the {@code zip} field. */
    protected ZipFileAdapter() { }

    /** Constructs a new ZIP file adapter for the given ZIP file. */
    public ZipFileAdapter(final @WillCloseWhenClosed ZipFile input) {
        this.zip = requireNonNull(input);
    }

    @Override public Iterator<ZipEntry> iterator() {
        return new Iterator<ZipEntry>() {
            final Enumeration<? extends ZipEntry> en = zip.entries();

            @Override public boolean hasNext() { return en.hasMoreElements(); }

            @Override public ZipEntry next() { return en.nextElement(); }

            @Override public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override public @Nullable ZipEntry entry(String name) {
        return zip.getEntry(name);
    }

    @Override public InputStream stream(ZipEntry entry) throws IOException {
        return zip.getInputStream(entry);
    }

    @Override public void close() throws IOException { zip.close(); }
}
