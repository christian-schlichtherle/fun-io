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
final class WithZipOutputTask<V> implements ZipSinks.ExecuteStatement<V> {

    private final ZipOutputTask<V> task;

    WithZipOutputTask(final ZipOutputTask<V> task) { this.task = task; }

    @Override
    public V on(File file) throws Exception { return on(new ZipFileStore(file)); }

    public V on(ZipSink sink) throws Exception { return Closeables.execute(task, sink.output()); }
}
