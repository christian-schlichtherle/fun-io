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

import java.io.OutputStream;

import static java.util.Objects.requireNonNull;

/**
 * Decorates output stream {@linkplain Socket sockets} in order to transform the transmitted content.
 * <p>
 * With an encryption filter for example, the {@link #output} method would decorate the loaned output streams
 * with a new {@link javax.crypto.CipherOutputStream} in order to encrypt the data before writing it to the underlying
 * output stream.
 * <p>
 * As another example, with a compression filter the {@link #output} method would decorate the loaned output
 * streams with a new {@link java.util.zip.DeflaterOutputStream} in order to compress the data before writing it to the
 * underlying output stream.
 * <p>
 * The benefit of this interface is that you can easily compose it in order to create rich filters without needing to
 * know anything about their implementation.
 * <p>
 * For the mathematically inclined, this class forms a
 * <a href="https://en.wikipedia.org/wiki/Group_(mathematics)">group</a> under its {@link #compose(OutputFilter)}
 * operator and its {@link #IDENTITY} instance.
 *
 * @author Christian Schlichtherle
 * @since 2.4.0
 */
@FunctionalInterface
public interface OutputFilter {

    /**
     * The identity output filter.
     */
    OutputFilter IDENTITY = new OutputFilter() {

        @Override
        public Socket<OutputStream> output(Socket<OutputStream> output) {
            return output;
        }

        @Override
        public Sink sink(Sink sink) {
            return sink;
        }

        @Override
        public OutputFilter compose(OutputFilter other) {
            return other;
        }
    };

    /**
     * Returns a filter which applies the other output filter AFTER this output filter.
     * For example, to compose an output filter which would first compress and then encrypt the data:
     * <pre>{@code
     * OutputFilter compression = [...];
     * OutputFilter encryption = [...];
     * OutputFilter compressionAndEncryption = compression.compose(encryption);
     * }</pre>
     */
    default OutputFilter compose(final OutputFilter other) {
        requireNonNull(other);
        return output -> output(other.output(output));
    }

    /**
     * Returns an output stream socket which applies this filter to the given output stream socket.
     */
    Socket<OutputStream> output(Socket<OutputStream> output);

    /**
     * Returns a sink which applies this filter to the given sink.
     */
    default Sink sink(Sink sink) {
        return () -> output(sink.output());
    }
}
