/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import global.namespace.fun.io.zip.io.Task;

import javax.annotation.WillNotClose;
import java.util.zip.ZipOutputStream;

/**
 * Executes a task on a {@link ZipOutputStream}.
 *
 * @see ZipSinks#execute
 * @author Christian Schlichtherle
 */
public interface ZipOutputTask<V, X extends Exception>
extends Task<V, ZipOutput, X> {

    @Override V execute(@WillNotClose ZipOutput resource) throws X;
}
