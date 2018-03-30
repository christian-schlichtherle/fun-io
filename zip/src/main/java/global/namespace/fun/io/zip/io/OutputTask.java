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
 * @see Sinks#execute
 * @author Christian Schlichtherle
 */
public interface OutputTask<V> extends Task<V, OutputStream> {

    @Override
    V execute(@WillNotClose OutputStream resource) throws Exception;
}
