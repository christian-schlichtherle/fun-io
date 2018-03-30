/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import global.namespace.fun.io.zip.io.Closeables;

import java.io.File;
import java.io.IOException;

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
