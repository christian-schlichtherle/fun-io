/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Socket;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.requireNonNull;

/**
 * A file based source and sink for ZIP files.
 *
 * @author Christian Schlichtherle
 */
public final class ZipStore implements ZipSource, ZipSink {

    private final File file;

    public ZipStore(final File file) { this.file = requireNonNull(file); }

    @Override
    public Socket<ZipInput> zipInput() { return () -> new ZipFileAdapter(new ZipFile(file)); }

    @Override
    public Socket<ZipOutput> zipOutput() {
        return () -> new ZipOutputStreamAdapter(new ZipOutputStream(new FileOutputStream(file)));
    }
}
