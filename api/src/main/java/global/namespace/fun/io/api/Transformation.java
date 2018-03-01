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
 * With an encryption transformation for example, the {@link #apply} method would decorate the loaned output streams
 * with a new {@link javax.crypto.CipherOutputStream} in order to encrypt the data before writing it to the underlying
 * output stream.
 * Likewise, the {@link #unapply} method would decorate the loaned input streams with a new
 * {@link javax.crypto.CipherInputStream} in order to decrypt the data after reading it from the underlying input
 * stream.
 * <p>
 * As another example, with a compression transformation the {@code apply} method would decorate the loaned output
 * streams with a new {@link java.util.zip.DeflaterOutputStream} in order to compress the data before writing it to the
 * underlying output stream.
 * Likewise, the {@code unapply} method would decorate the loaned input streams with a new
 * {@link java.util.zip.InflaterInputStream} in order to decompress the data after reading it from the underlying input
 * stream.
 * <p>
 * The benefit of this interface is that you can easily chain the {@code apply} and {@code unapply} methods in order to
 * from rich decorators without needing to know anything about the implementation of the transformations.
 * <p>
 * For example, depending on the previous examples, the following test code would assert the round-trip processing of
 * the string {@code "Hello world!"} using the composition of some compression and encryption transformations on
 * some store:
 * <pre>{@code
 * Transformation compression = ...;
 * Transformation encryption = ...;
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
public interface Transformation {

    /** The identity transformation. */
    Transformation IDENTITY = new Transformation() {

        @Override
        public Socket<OutputStream> apply(Socket<OutputStream> output) { return output; }

        @Override
        public Socket<InputStream> unapply(Socket<InputStream> input) { return input; }

        @Override
        public Transformation inverse() { return this; }
    };

    /** Returns an output stream socket which decorates the given output stream socket. */
    Socket<OutputStream> apply(Socket<OutputStream> output);

    /** Returns an input stream socket which decorates the given input stream socket. */
    Socket<InputStream> unapply(Socket<InputStream> input);

    /**
     * Returns the inverse of this transformation (optional operation).
     * An implementation may choose to throw an {@link UnsupportedOperationException} if inverting this transformation
     * is not supported.
     * However, it's strongly encouraged to provide a proper implementation of this operation because it's trivially
     * possible to invert any transformation by buffering its entire content.
     *
     * @throws UnsupportedOperationException if inverting this transformation is not supported.
     */
    Transformation inverse();

    /** Returns a transformation which applies the given transformation <em>before</em> this transformation. */
    default Transformation compose(Transformation before) { return Internal.compose(requireNonNull(before), this); }

    /** Returns a transformation which applies the given transformation <em>after</em> this transformation. */
    default Transformation andThen(Transformation after) { return Internal.compose(this, requireNonNull(after)); }
}
