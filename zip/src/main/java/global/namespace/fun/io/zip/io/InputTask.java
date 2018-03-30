/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import javax.annotation.WillNotClose;
import java.io.InputStream;

/**
 * Executes a task on an {@link InputStream}.
 *
 * @param <V> the type of the result.
 * @see Sources#execute
 * @author Christian Schlichtherle
 */
public interface InputTask<V> extends Task<V, InputStream> {

    @Override
    V execute(@WillNotClose InputStream resource) throws Exception;
}
