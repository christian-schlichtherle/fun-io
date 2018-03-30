/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.api.Socket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.requireNonNull;

/**
 * Writes a ZIP entry to a ZIP output.
 *
 * @see ZipEntrySource
 * @author Christian Schlichtherle
 */
public final class ZipEntrySink implements Sink {

    private final ZipEntry entry;
    private final ZipOutput output;

    public ZipEntrySink(final ZipEntry entry, final ZipOutput output) {
        this.entry = requireNonNull(entry);
        this.output = requireNonNull(output);
    }

    /** Returns the entry name. */
    public String name() { return entry.getName(); }

    /** Returns {@code true} if the entry is a directory entry. */
    public boolean directory() { return entry.isDirectory(); }

    @Override
    public Socket<OutputStream> output() {
        return () -> {
            if (directory()) {
                entry.setMethod(ZipOutputStream.STORED);
                entry.setSize(0);
                entry.setCompressedSize(0);
                entry.setCrc(0);
            }
            return output.stream(entry);
        };
    }
}
