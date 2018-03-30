/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Socket;

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

    private final ZipOutputStream zip;

    public ZipOutputStreamAdapter(final @WillCloseWhenClosed ZipOutputStream zip) { this.zip = requireNonNull(zip); }

    @Override
    public ZipEntry entry(String name) { return new ZipEntry(name); }

    @Override
    public Socket<OutputStream> output(final ZipEntry entry) {
        return () -> {
            if (entry.isDirectory()) {
                entry.setMethod(ZipOutputStream.STORED);
                entry.setSize(0);
                entry.setCompressedSize(0);
                entry.setCrc(0);
            }
            zip.putNextEntry(entry);
            return new FilterOutputStream(zip) {
                @Override public void close() throws IOException {
                    ((ZipOutputStream) out).closeEntry();
                }
            };
        };
    }

    @Override
    public void close() throws IOException { zip.close(); }
}
