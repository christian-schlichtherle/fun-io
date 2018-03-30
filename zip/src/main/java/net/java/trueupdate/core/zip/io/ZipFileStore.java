/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.requireNonNull;

/**
 * A file based ZIP store.
 *
 * @author Christian Schlichtherle
 */
public class ZipFileStore implements ZipStore {

    final File file;

    public ZipFileStore(final File file) {
        this.file = requireNonNull(file);
    }

    @Override public ZipInput input() throws IOException {
        return new ZipFileAdapter(new ZipFile(file));
    }

    @Override public ZipOutput output() throws IOException {
        return new ZipOutputStreamAdapter(new ZipOutputStream(
                new FileOutputStream(file)));
    }

    @Override public void delete() throws IOException {
        if (!file.delete() && file.exists())
            throw new IOException(file + " (could not delete)");
    }

    @Override public boolean exists() { return file.exists(); }
}
