/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import java.io.File;
import java.io.IOException;

/**
 * @see Sinks#execute
 * @author Christian Schlichtherle
 */
final class WithOutputTask<V> implements Sinks.ExecuteStatement<V> {

    private final OutputTask<V> task;

    WithOutputTask(final OutputTask<V> task) { this.task = task; }

    @Override public V on(File file) throws Exception {
        return on(new FileStore(file));
    }

    @Override public V on(Sink sink) throws Exception {
        return Closeables.execute(task, sink.output());
    }
}
