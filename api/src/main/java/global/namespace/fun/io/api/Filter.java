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

import java.io.InputStream;
import java.io.OutputStream;

import static java.util.Objects.requireNonNull;

/**
 * Decorates input and output stream {@linkplain Socket sockets} in order to transform the transmitted content.
 * <p>
 * With an encryption filter for example, the {@link #apply} method would decorate the loaned output streams
 * with a new {@link javax.crypto.CipherOutputStream} in order to encrypt the data before writing it to the underlying
 * output stream.
 * Likewise, the {@link #unapply} method would decorate the loaned input streams with a new
 * {@link javax.crypto.CipherInputStream} in order to decrypt the data after reading it from the underlying input
 * stream.
 * <p>
 * As another example, with a compression filter the {@code apply} method would decorate the loaned output
 * streams with a new {@link java.util.zip.DeflaterOutputStream} in order to compress the data before writing it to the
 * underlying output stream.
 * Likewise, the {@code unapply} method would decorate the loaned input streams with a new
 * {@link java.util.zip.InflaterInputStream} in order to decompress the data after reading it from the underlying input
 * stream.
 * <p>
 * The benefit of this interface is that you can easily chain the {@code apply} and {@code unapply} methods in order to
 * from rich decorators without needing to know anything about the implementation of the filters.
 * <p>
 * For example, depending on the previous examples, the following test code would assert the round-trip processing of
 * the string {@code "Hello world!"} using the composition of some compression and encryption filters on
 * some store:
 * <pre>{@code
 * Filter compression = ...;
 * Filter encryption = ...;
 * Store store = ...;
 * Socket<OutputStream> compressAndEncryptData = compression.apply(encryption.apply(store.output()));
 * Socket<InputStream> decryptAndDecompressData = compression.unapply(encryption.unapply(store.input()));
 * compressAndEncryptData.map(PrintWriter::new).accept(writer -> writer.println("Hello world!"));
 * decryptAndDecompressData.map(InputStreamReader::new).map(BufferedReader::new).accept(reader ->
 *     assertTrue("Hello world!".equals(reader.readLine())));
 * }</pre>
 *
 * @author Christian Schlichtherle
 */
public interface Filter {

    /** The identity filter. */
    Filter IDENTITY = new Filter() {

        @Override
        public Socket<OutputStream> apply(Socket<OutputStream> output) { return output; }

        @Override
        public Socket<InputStream> unapply(Socket<InputStream> input) { return input; }

        @Override
        public Filter inverse() { return this; }
    };

    /** Returns an output stream socket which decorates the given output stream socket. */
    Socket<OutputStream> apply(Socket<OutputStream> output);

    /** Returns a sink which decorates the given sink. */
    default Sink apply(Sink sink) { return () -> apply(sink.output()); }

    /** Returns an input stream socket which decorates the given input stream socket. */
    Socket<InputStream> unapply(Socket<InputStream> input);

    /** Returns a source which decorates the given source. */
    default Source unapply(Source source) { return () -> unapply(source.input()); };

    /**
     * Returns the inverse of this filter (optional operation).
     * An implementation may choose to throw an {@link UnsupportedOperationException} if inverting this filter
     * is not supported.
     * However, it's strongly encouraged to provide a proper implementation of this operation because it's trivially
     * possible to invert any filter by buffering its entire content.
     *
     * @throws UnsupportedOperationException if inverting this filter is not supported.
     */
    Filter inverse();

    /** Returns a filter which applies the given filter <em>before</em> this filter. */
    default Filter compose(Filter before) { return Internal.compose(requireNonNull(before), this); }

    /** Returns a filter which applies the given filter <em>after</em> this filter. */
    default Filter andThen(Filter after) { return Internal.compose(this, requireNonNull(after)); }
}
