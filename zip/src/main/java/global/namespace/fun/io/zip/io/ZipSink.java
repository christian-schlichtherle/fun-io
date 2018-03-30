/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.api.function.XFunction;

/**
 * An abstraction for safe writing of ZIP file entries without leaking resources.
 * A ZIP sink provides a {@linkplain #output() socket} for safe access to a {@linkplain ZipOutput ZIP output}.
 *
 * @author Christian Schlichtherle
 */
public interface ZipSink {

    /** Returns the underlying socket for writing the ZIP entries. */
    Socket<ZipOutput> output();

    /**
     * Loans a ZIP output from the underlying {@linkplain #output() socket} to the given consumer.
     * The ZIP output will be closed upon return from this method.
     */
    default void acceptWriter(XConsumer<? super ZipOutput> writer) throws Exception { output().accept(writer); }

    /**
     * Loans a ZIP output from the underlying {@linkplain #output() socket} to the given function and returns its value.
     * The ZIP output will be closed upon return from this method.
     * <p>
     * It is an error to return the loaned ZIP output from the given function or any other object which holds on to it.
     */
    default <U> U applyWriter(XFunction<? super ZipOutput, ? extends U> writer) throws Exception {
        return output().apply(writer);
    }
}
