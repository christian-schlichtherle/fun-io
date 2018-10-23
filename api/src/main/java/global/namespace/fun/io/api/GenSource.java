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

/**
 * A generic abstraction for safe access to some closeable input resource - DO NOT USE THIS DIRECTLY!
 * This type primarily exists to enforce consistency between {@link Source} and {@link ArchiveSource}.
 *
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface GenSource<T extends AutoCloseable> {

    /**
     * Returns an input socket for safe access to some closeable input resource.
     */
    Socket<T> input();

    /**
     * Loans a closeable resource from the {@linkplain #input() input socket} to the given consumer.
     * The loaned resource will be closed upon return from this method.
     */
    default void acceptReader(XConsumer<? super T> reader) throws Exception {
        input().accept(reader);
    }

    /**
     * Loans a closeable resource from the {@linkplain #input() input socket} to the given function and returns its
     * value.
     * The loaned resource will be closed upon return from this method.
     * It is an error to return the loaned resource from the given function or any other object which holds on to it.
     */
    default <U> U applyReader(XFunction<? super T, ? extends U> reader) throws Exception {
        return input().apply(reader);
    }
}
