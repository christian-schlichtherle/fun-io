/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import global.namespace.fun.io.zip.io.Task;

import javax.annotation.WillNotClose;
import java.util.zip.ZipFile;

/**
 * Executes a task on a {@link ZipFile}.
 *
 * @see ZipSources#execute
 * @author Christian Schlichtherle
 */
public interface ZipInputTask<V> extends Task<V, ZipInput> {

    @Override
    V execute(@WillNotClose ZipInput resource) throws Exception;
}
