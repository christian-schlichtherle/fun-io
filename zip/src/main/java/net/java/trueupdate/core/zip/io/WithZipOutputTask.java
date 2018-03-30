/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.io;

import java.io.*;
import net.java.trueupdate.core.io.*;

/**
 * @see ZipSinks#execute
 * @author Christian Schlichtherle
 */
final class WithZipOutputTask<V, X extends Exception>
implements ZipSinks.ExecuteStatement<V, X> {

    private final ZipOutputTask<V, X> task;

    WithZipOutputTask(final ZipOutputTask<V, X> task) { this.task = task; }

    @Override public V on(File file) throws X, IOException {
        return on(new ZipFileStore(file));
    }

    @Override public V on(ZipSink sink) throws X, IOException {
        return Closeables.execute(task, sink.output());
    }
}
