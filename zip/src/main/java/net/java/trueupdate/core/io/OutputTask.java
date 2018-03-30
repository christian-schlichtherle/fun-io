/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.io;

import java.io.OutputStream;
import javax.annotation.WillNotClose;

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
