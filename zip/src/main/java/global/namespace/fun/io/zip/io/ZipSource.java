/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.api.function.XFunction;

/**
 * An abstraction for safe reading of ZIP entries without leaking resources.
 * A ZIP source provides a {@linkplain #zipInput() socket} for safe access to a {@linkplain ZipInput ZIP input}.
 *
 * @author Christian Schlichtherle
 */
public interface ZipSource {

    /** Returns the underlying ZIP input socket for reading the ZIP entries. */
    Socket<ZipInput> zipInput();

    /**
     * Loans a ZIP input from the underlying {@linkplain #zipInput() socket} to the given consumer.
     * The ZIP input will be closed upon return from this method.
     */
    default void acceptZipReader(XConsumer<? super ZipInput> reader) throws Exception { zipInput().accept(reader); }

    /**
     * Loans a ZIP input from the underlying {@linkplain #zipInput() socket} to the given function and returns its value.
     * The ZIP input will be closed upon return from this method.
     * <p>
     * It is an error to return the loaned ZIP input from the given function or any other object which holds on to it.
     */
    default <U> U applyZipReader(XFunction<? super ZipInput, ? extends U> reader) throws Exception {
        return zipInput().apply(reader);
    }
}
