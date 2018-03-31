/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.api.function.XFunction;

/**
 * An abstraction for safe writing of ZIP entries without leaking resources.
 * A ZIP sink provides a {@linkplain #zipOutput() socket} for safe access to a {@linkplain ZipOutput ZIP output}.
 *
 * @author Christian Schlichtherle
 */
public interface ZipSink {

    /** Returns the underlying ZIP output socket for writing the ZIP entries. */
    Socket<ZipOutput> zipOutput();

    /**
     * Loans a ZIP output from the underlying {@linkplain #zipOutput() socket} to the given consumer.
     * The ZIP output will be closed upon return from this method.
     */
    default void acceptZipWriter(XConsumer<? super ZipOutput> writer) throws Exception { zipOutput().accept(writer); }

    /**
     * Loans a ZIP output from the underlying {@linkplain #zipOutput() socket} to the given function and returns its value.
     * The ZIP output will be closed upon return from this method.
     * <p>
     * It is an error to return the loaned ZIP output from the given function or any other object which holds on to it.
     */
    default <U> U applyZipWriter(XFunction<? super ZipOutput, ? extends U> writer) throws Exception {
        return zipOutput().apply(writer);
    }
}
