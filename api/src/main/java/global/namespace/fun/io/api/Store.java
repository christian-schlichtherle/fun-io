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

import java.io.*;
import java.util.OptionalLong;

import static java.util.Objects.requireNonNull;

/**
 * Emits input and output stream {@linkplain Socket sockets} in order to enable access to its stored content.
 *
 * @author Christian Schlichtherle
 */
public interface Store extends Source, Sink {

    /** The default buffer size, which is {@value}. */
    int BUFSIZE = 8 * 1024;

    /** Deletes the content of this store. */
    void delete() throws IOException;

    /** Returns the size of the content of this store, if present. */
    OptionalLong size() throws IOException;

    /** Returns {@code true} if and only if some content exists in this store. */
    default boolean exists() throws IOException { return size().isPresent(); }

    /** Connects this store to the given codec. */
    default ConnectedCodec connect(Codec c) { return Internal.connect(requireNonNull(c), this); }

    /**
     * Returns a store which applies the given filter to the I/O streams loaned by this store.
     *
     * @param t the filter to apply to the I/O streams loaned by this store.
     */
    default Store map(Filter t) {
        return new Store() {

            @Override
            public Socket<InputStream> input() { return t.unapply(Store.this.input()); }

            @Override
            public Socket<OutputStream> output() { return t.apply(Store.this.output()); }

            @Override
            public void delete() throws IOException { Store.this.delete(); }

            @Override
            public OptionalLong size() throws IOException { return Store.this.size(); }
        };
    }

    /**
     * Returns the content of this store.
     *
     * @throws ContentTooLargeException if the content exceeds {@link Integer#MAX_VALUE} bytes.
     * @throws IOException if there is no content or if the content cannot be read for some reason.
     */
    default byte[] content() throws IOException { return content(Integer.MAX_VALUE); }

    /**
     * Returns the content of this store.
     *
     * @throws IllegalArgumentException if {@code max} is less than zero.
     * @throws ContentTooLargeException if the content exceeds {@code max } bytes.
     * @throws IOException if there is no content or if the content cannot be read for some reason.
     */
    default byte[] content(final int max) throws IOException {
        if (max < 0) {
            throw new IllegalArgumentException(max + " < 0");
        }
        final OptionalLong size = size();
        if (size.isPresent()) {
            final long length = size.getAsLong();
            if (length <= max) {
                final byte[] content = new byte[(int) length];
                try {
                    input().map(DataInputStream::new).accept(in -> in.readFully(content));
                } catch (IOException | RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IOException(e);
                }
                return content;
            } else {
                throw new ContentTooLargeException(length, max);
            }
        } else {
            throw new NoContentException();
        }
    }

    /** Sets the content of this store from the given byte array. */
    default void content(byte[] b) throws IOException {
        content(b, 0, b.length);
    }

    /** Sets the content of this store from the given byte array at the given offset and length. */
    default void content(final byte[] b, final int off, final int len) throws IOException {
        try {
            acceptWriter(out -> out.write(b, off, len));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
