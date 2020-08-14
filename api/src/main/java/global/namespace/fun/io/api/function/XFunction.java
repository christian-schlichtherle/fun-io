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
package global.namespace.fun.io.api.function;

import java.util.Objects;

/**
 * Like {@link java.util.function.Function}, but may throw any type of exception.
 *
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface XFunction<T, R> {

    R apply(T t) throws Exception;

    default <V> XFunction<V, R> compose(XFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return v -> apply(before.apply(v));
    }

    default <V> XFunction<T, V> andThen(XFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }

    static <T> XFunction<T, T> identity() { return t -> t; }
}
