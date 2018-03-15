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

/**
 * Encapsulated a {@link #codec() codec} and a {@link #store() store}.
 *
 * @author Christian Schlichtherle
 */
public interface ConnectedCodec extends Encoder, Decoder {

    /** Returns the underlying codec. */
    Codec codec();

    /** Returns the underlying store. */
    Store store();

    default void encode(Object o) throws Exception { codec().encoder(store()).encode(o); }

    default <T> T decode(Class<T> expected) throws Exception { return codec().decoder(store()).decode(expected); }

    /** Returns a deep clone of the given object by encoding it to the underlying store and decoding it again. */
    @SuppressWarnings("unchecked")
    default <T> T clone(T t) throws Exception {
        encode(t);
        return decode((Class<? extends T>) t.getClass());
    }
}
