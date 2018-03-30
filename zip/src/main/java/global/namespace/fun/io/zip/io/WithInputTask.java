/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import java.io.File;
import java.io.IOException;

/**
 * @see Sources#execute
 * @author Christian Schlichtherle
 */
final class WithInputTask<V> implements Sources.ExecuteStatement<V> {

    private final InputTask<V> task;

    WithInputTask(final InputTask<V> task) { this.task = task; }

    @Override public V on(File file) throws Exception {
        return on(new FileStore(file));
    }

    @Override public V on(Source source) throws Exception {
        return Closeables.execute(task, source.input());
    }
}
