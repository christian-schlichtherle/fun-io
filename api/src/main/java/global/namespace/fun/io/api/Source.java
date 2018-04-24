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

import java.io.InputStream;

/**
 * An abstraction for safe reading from input streams without leaking resources.
 * A source provides a {@linkplain #input() socket} for safe access to an {@linkplain InputStream input stream}.
 *
 * @author Christian Schlichtherle
 */
public interface Source {

    /** Returns the underlying input stream socket for reading the content of this source. */
    Socket<InputStream> input();

    /**
     * Loans an input stream from the underlying {@linkplain #input() socket} to the given consumer.
     * The input stream will be closed upon return from this method.
     */
    default void acceptReader(XConsumer<? super InputStream> reader) throws Exception { input().accept(reader); }

    /**
     * Loans an input stream from the underlying {@linkplain #input() socket} to the given function and returns its
     * value.
     * The input stream will be closed upon return from this method.
     * <p>
     * It is an error to return the loaned input stream from the given function or any other object which holds on to
     * it.
     * Use the {@link #map(Filter)} method instead if you need to transform the underlying input stream socket.
     */
    default <U> U applyReader(XFunction<? super InputStream, ? extends U> reader) throws Exception {
        return input().apply(reader);
    }

    /**
     * Returns a source which applies the given filter to the I/O streams loaned by the underlying
     * {@linkplain #input() input stream socket}.
     *
     * @param t the filter to apply to the I/O streams loaned by the underlying input stream socket.
     */
    default Source map(Filter t) { return () -> t.unapply(input()); }
}
