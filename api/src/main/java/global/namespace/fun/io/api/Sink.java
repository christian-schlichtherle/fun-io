/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.fun.io.api;

import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.api.function.XFunction;

import java.io.OutputStream;

/**
 * An abstraction for safe writing to an output stream without leaking resources.
 * A sink provides a {@linkplain #output()} socket} for safe access to an {@linkplain OutputStream output stream}.
 *
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface Sink {

    /**
     * Returns the underlying output stream socket for (over)writing the content of this sink.
     */
    Socket<OutputStream> output();

    /**
     * Loans an output stream from the underlying {@linkplain #output() socket} to the given consumer.
     * The output stream will be closed upon return from this method.
     */
    default void acceptWriter(XConsumer<? super OutputStream> writer) throws Exception {
        output().accept(writer);
    }

    /**
     * Loans an output stream from the underlying {@linkplain #output() socket} to the given function and returns its
     * value.
     * The output stream will be closed upon return from this method.
     * <p>
     * It is an error to return the loaned output stream from the given function or any other object which holds on to
     * it.
     * Use the {@link #map(Filter)} method instead if you need to transform the underlying output stream socket.
     */
    default <U> U applyWriter(XFunction<? super OutputStream, ? extends U> writer) throws Exception {
        return output().apply(writer);
    }

    /**
     * Returns a sink which applies the given filter to this sink.
     *
     * @param f the filter to apply to this sink.
     */
    default Sink map(Filter f) {
        return f.sink(this);
    }
}
