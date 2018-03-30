/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.api.Socket;

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

    @Override
    public Socket<OutputStream> output() { return output.output(entry); }
}
