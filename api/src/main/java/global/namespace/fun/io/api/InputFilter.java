/*
 * Copyright © 2017 - 2020 Schlichtherle IT Services
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

import java.io.InputStream;

import static java.util.Objects.requireNonNull;

/**
 * Decorates input stream {@linkplain Socket sockets} in order to transform the transmitted content.
 * <p>
 * With an decryption input filter for example, the {@link #input} method would decorate the loaned input streams with a
 * new {@link javax.crypto.CipherInputStream} in order to decrypt the data after reading it from the underlying input
 * stream.
 * <p>
 * As another example, with a decompression input filter the {@link #input} method would decorate the loaned input
 * streams with a new {@link java.util.zip.InflaterInputStream} in order to decompress the data after reading it from
 * the underlying input stream.
 * <p>
 * The benefit of this interface is that you can easily compose it in order to create rich filters without needing to
 * know anything about their implementation.
 * <p>
 * For the mathematically inclined, this class forms a
 * <a href="https://en.wikipedia.org/wiki/Group_(mathematics)">group</a> under its {@link #compose(InputFilter)}
 * operator and its {@link #IDENTITY} instance.
 *
 * @author Christian Schlichtherle
 * @since 2.4.0
 */
@FunctionalInterface
public interface InputFilter {

    /**
     * The identity input filter.
     */
    InputFilter IDENTITY = new InputFilter() {

        @Override
        public Socket<InputStream> input(Socket<InputStream> input) {
            return input;
        }

        @Override
        public Source source(Source source) {
            return source;
        }

        @Override
        public InputFilter compose(InputFilter other) {
            return other;
        }
    };

    /**
     * Returns an input filter which applies the other input filter BEFORE this input filter.
     * For example, to compose an input filter which would first decrypt and then decompress the data:
     * <pre>{@code
     * InputFilter decompression = [...];
     * InputFilter decryption = [...];
     * InputFilter decryptionAndDecompression = decompression.compose(decryption);
     * }</pre>
     */
    default InputFilter compose(final InputFilter other) {
        requireNonNull(other);
        return input -> input(other.input(input));
    }

    /**
     * Returns an input stream socket which applies this filter to the given input stream socket.
     */
    Socket<InputStream> input(Socket<InputStream> input);

    /**
     * Returns a source which applies this filter to the given source.
     */
    default Source source(Source source) {
        return () -> input(source.input());
    }
}
