/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.api.function.XFunction;

/**
 * A generic abstraction for safe access to some closeable output resource - DO NOT USE THIS DIRECTLY!
 * This type primarily exists to enforce consistency between {@link Sink} and {@link ArchiveSink}.
 *
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface GenSink<T extends AutoCloseable> {

    /**
     * Returns an output socket for safe access to some closeable output resource.
     */
    Socket<T> output();

    /**
     * Loans a closeable resource from the {@linkplain #output() output socket} to the given consumer.
     * The loaned resource will be closed upon return from this method.
     */
    default void acceptWriter(XConsumer<? super T> writer) throws Exception {
        output().accept(writer);
    }

    /**
     * Loans a closeable resource from the {@linkplain #output() output socket} to the given function and returns its
     * value.
     * The loaned resource will be closed upon return from this method.
     * It is an error to return the loaned resource from the given function or any other object which holds on to it.
     */
    default <U> U applyWriter(XFunction<? super T, ? extends U> writer) throws Exception {
        return output().apply(writer);
    }
}
