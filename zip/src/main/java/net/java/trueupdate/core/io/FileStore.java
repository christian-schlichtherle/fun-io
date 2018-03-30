/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * Copyright (C) 2013 Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.io;

import java.io.*;

import static java.util.Objects.requireNonNull;

/**
 * A file store.
 *
 * @author Christian Schlichtherle (copied and edited from TrueLicense Core 2.3.1)
 */
public final class FileStore implements Store {

    private final File file;

    public FileStore(final File file) { this.file = requireNonNull(file); }

    @Override public InputStream input() throws IOException {
        return new FileInputStream(file);
    }

    @Override public OutputStream output() throws IOException {
        return new FileOutputStream(file);
    }

    @Override public void delete() throws IOException {
        if (!file.delete())
            throw new FileNotFoundException(file + " (could not delete)");
    }

    @Override public boolean exists() { return file.exists(); }
}
