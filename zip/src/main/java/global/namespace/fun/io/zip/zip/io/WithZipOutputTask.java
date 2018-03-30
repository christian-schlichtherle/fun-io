/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import java.io.File;

/**
 * @author Christian Schlichtherle
 */
final class WithZipOutputTask<V> implements ZipSinks.ExecuteStatement<V> {

    private final ZipOutputTask<V> task;

    WithZipOutputTask(final ZipOutputTask<V> task) { this.task = task; }

    @Override
    public V on(File file) throws Exception { return on(new ZipFileStore(file)); }

    @Override
    public V on(ZipSink sink) throws Exception { return sink.applyWriter(task::execute);  }
}
