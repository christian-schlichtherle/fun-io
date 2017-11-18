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
import global.namespace.fun.io.api.function.XSupplier;

import java.util.Objects;

/**
 * Loans an auto-closeable resource obtained from an underlying supplier to a consumer or a function.
 * A loan is reusable if and only if the underlying resource supplier is reusable.
 *
 * @param <T> the type of the auto-closeable resource.
 * @author Christian Schlichtherle
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@FunctionalInterface
public interface Loan<T extends AutoCloseable> extends XSupplier<T> {

    /**
     * Returns a resource for direct access by the caller.
     *
     * @deprecated While being convenient, this method should not be used because it would defeat the purpose of this
     *             interface.
     */
    @Deprecated
    T get() throws Exception;

    /**
     * Loans a resource obtained from the underlying supplier to the given consumer.
     * Upon return from this method, the resource will be closed.
     */
    default void accept(final XConsumer<? super T> c) throws Exception {
        try (T resource = get()) {
            c.accept(resource);
        }
    }

    /**
     * Loans a resource obtained from the underlying supplier to the given function and returns its value.
     * Upon return from this method, the resource will be closed.
     * <p>
     * It is an error to return the loaned resource from the given function or any other object which holds on to it.
     * Use the {@link #map(XFunction)} or {@link #flatMap(XFunction)} methods instead to transform the resource.
     */
    default <U> U apply(final XFunction<? super T, ? extends U> f) throws Exception {
        try (T resource = get()) {
            return f.apply(resource);
        }
    }

    /** Transforms the resources obtained from the underlying supplier using the given function. */
    default <U extends AutoCloseable> Loan<U> map(XFunction<? super T, ? extends U> f) {
        Objects.requireNonNull(f);
        return () -> {
            final T resource = get();
            try {
                return f.apply(resource);
            } catch (final Throwable t1) {
                try {
                    resource.close();
                } catch (Throwable t2) {
                    t1.addSuppressed(t2);
                }
                throw t1;
            }
        };
    }

    /** Transforms the resources obtained from the underlying supplier using the given function. */
    default <U extends AutoCloseable> Loan<U> flatMap(XFunction<? super T, ? extends Loan<? extends U>> f) {
        return map(f.andThen(Loan::get));
    }
}
