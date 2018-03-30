/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

/**
 * When executed, a task operates on a resource and returns a result or throws
 * an exception.
 *
 * @param <V> the type of the result.
 * @param <R> the type of the resource.
 * @param <X> the type of the exception.
 * @author Christian Schlichtherle
 */
public interface Task<V, R, X extends Exception> {

    V execute(R resource) throws X;
}
