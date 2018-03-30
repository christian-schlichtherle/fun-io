/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import javax.annotation.WillCloseWhenClosed;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.requireNonNull;

/**
 * Adapts a {@link ZipOutputStream} to a {@link ZipOutput}.
 *
 * @see ZipFileAdapter
 * @author Christian Schlichtherle
 */
public class ZipOutputStreamAdapter implements ZipOutput {

    /** The adapted ZIP output stream. */
    protected ZipOutputStream zip;

    /** Use of this constructor requires setting the {@code zip} field. */
    protected ZipOutputStreamAdapter() { }

    /**
     * Constructs a new ZIP output stream adapter for the given ZIP output
     * stream.
     */
    public ZipOutputStreamAdapter(final @WillCloseWhenClosed ZipOutputStream zip) {
        this.zip = requireNonNull(zip);
    }

    @Override public ZipEntry entry(String name) { return new ZipEntry(name); }

    @Override
    public OutputStream stream(final ZipEntry entry) throws IOException {
        zip.putNextEntry(entry);
        return new FilterOutputStream(zip) {
            @Override public void close() throws IOException {
                ((ZipOutputStream) out).closeEntry();
            }
        };
    }

    @Override public void close() throws IOException { zip.close(); }
}
