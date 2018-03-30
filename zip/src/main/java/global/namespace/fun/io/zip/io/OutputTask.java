/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import javax.annotation.WillNotClose;
import java.io.OutputStream;

/**
 * Executes a task on an {@link OutputStream}.
 *
 * @param <V> the type of the result.
 * @param <X> the type of the exception.
 * @see Sinks#execute
 * @author Christian Schlichtherle
 */
public interface OutputTask<V, X extends Exception>
extends Task<V, OutputStream, X> {

    @Override V execute(@WillNotClose OutputStream resource) throws X;
}
