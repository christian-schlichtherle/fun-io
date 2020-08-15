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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.OptionalLong;

import static java.util.Objects.requireNonNull;

/**
 * Decorates input and output stream {@linkplain Socket sockets} in order to transform the transmitted content.
 * <p>
 * With an encryption filter for example, the {@link #output} method would decorate the loaned output streams
 * with a new {@link javax.crypto.CipherOutputStream} in order to encrypt the data before writing it to the underlying
 * output stream.
 * Likewise, the {@link #input} method would decorate the loaned input streams with a new
 * {@link javax.crypto.CipherInputStream} in order to decrypt the data after reading it from the underlying input
 * stream.
 * <p>
 * As another example, with a compression filter the {@link #output} method would decorate the loaned output
 * streams with a new {@link java.util.zip.DeflaterOutputStream} in order to compress the data before writing it to the
 * underlying output stream.
 * Likewise, the {@link #input} method would decorate the loaned input streams with a new
 * {@link java.util.zip.InflaterInputStream} in order to decompress the data after reading it from the underlying input
 * stream.
 * <p>
 * The benefit of this interface is that you can easily compose it in order to create rich filters without needing to
 * know anything about their implementation.
 * <p>
 * For example, depending on the previous examples, the following test code would assert the round-trip processing of
 * the string {@code "Hello world!"} using the composition of some compression and encryption filters on
 * some store:
 * <pre>{@code
 * Filter compression = ...;
 * Filter encryption = ...;
 * Store store = ...;
 * Store compressedAndEncryptedStore = store.map(compression.compose(encryption));
 * // ... which is the same as:
 * // Store compressedAndEncryptedStore = store.map(encryption).map(compression);
 * Socket<OutputStream> compressedAndEncryptedData = store.output();
 * Socket<InputStream> decryptedAndDecompressedData = store.input();
 * compressedAndEncryptedData.map(PrintWriter::new).accept(writer -> writer.println("Hello world!"));
 * decryptedAndDecompressedData.map(InputStreamReader::new).map(BufferedReader::new).accept(reader ->
 *     assertTrue("Hello world!".equals(reader.readLine())));
 * }</pre>
 * <p>
 * For the mathematically inclined, this class forms a
 * <a href="https://en.wikipedia.org/wiki/Group_(mathematics)">group</a> under its {@link #compose(Filter)}
 * operator and its {@link #IDENTITY} instance.
 *
 * @author Christian Schlichtherle
 */
public interface Filter extends InputFilter, OutputFilter {

    /**
     * The identity filter.
     */
    Filter IDENTITY = new Filter() {

        @Override
        public Socket<OutputStream> output(Socket<OutputStream> output) {
            return output;
        }

        @Override
        public Socket<InputStream> input(Socket<InputStream> input) {
            return input;
        }

        @Override
        public Sink sink(Sink sink) {
            return sink;
        }

        @Override
        public Source source(Source source) {
            return source;
        }

        @Override
        public Store store(Store store) {
            return store;
        }

        @Override
        public Codec codec(Codec codec) {
            return codec;
        }

        @Override
        public Filter compose(Filter other) {
            return other;
        }

        @Override
        @Deprecated
        public Filter andThen(Filter other) {
            return other;
        }
    };

    /**
     * Returns a filter which applies the other filter AFTER this filter on output and BEFORE this filter on input.
     * For example, to compose a filter which would first compress and then encrypt the data on output:
     * <pre>{@code
     * Filter compression = [...];
     * Filter encryption = [...];
     * Filter compressionAndEncryption = compression.compose(encryption);
     * }</pre>
     * On input, the filter would first decrypt the data and then decompress it.
     * <p>
     * Note that before version 2.3.0, this method was erroneously documented to compose the filters in the opposite
     * order.
     */
    default Filter compose(Filter other) {
        return Internal.compose(this, requireNonNull(other));
    }

    /**
     * Returns a filter which applies the other filter BEFORE this filter on output and AFTER this filter on input.
     *
     * @deprecated since 2.3.0: The name of this method is completely misleading and in previous versions, it was
     *             erroneously documented to compose the filters in the opposite order - <strong>DO NOT USE</strong>!
     */
    @Deprecated
    default Filter andThen(Filter other) {
        return other.compose(this);
    }

    /**
     * Returns a store which applies this filter to the given store.
     *
     * @param store the store to apply this filter to.
     */
    default Store store(Store store) {
        return new Store() {

            @Override
            public Socket<OutputStream> output() {
                return Filter.this.output(store.output());
            }

            @Override
            public Socket<InputStream> input() {
                return Filter.this.input(store.input());
            }

            @Override
            public void delete() throws IOException {
                store.delete();
            }

            @Override
            public OptionalLong size() throws IOException {
                return store.size();
            }

            @Override
            public byte[] content(final int max) throws IOException {
                if (max < 0) {
                    throw new IllegalArgumentException(max + " < 0");
                }
                try {
                    return applyReader(in -> {
                        final ByteArrayOutputStream out = new ByteArrayOutputStream(BUFSIZE);
                        final byte[] b = new byte[BUFSIZE];
                        for (int total = 0, n; 0 <= (n = in.read(b)); ) {
                            if (max < (total += n)) {
                                throw new ContentTooLargeException(total, max);
                            }
                            out.write(b, 0, n);
                        }
                        return out.toByteArray();
                    });
                } catch (IOException | RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
        };
    }

    /**
     * Returns a codec which applies this filter to the the given codec.
     *
     * @param codec the codec to apply this filter to.
     */
    default Codec codec(Codec codec) {
        return new Codec() {

            @Override
            public Encoder encoder(Socket<OutputStream> output) {
                return codec.encoder(output(output));
            }

            @Override
            public Decoder decoder(Socket<InputStream> input) {
                return codec.decoder(input(input));
            }
        };
    }
}
