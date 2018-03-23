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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * Returns a store which applies the given transformation to the I/O streams loaned by this store.
     *
     * @param t the transformation to apply to the I/O streams loaned by this store.
     */
    default Store map(Transformation t) {
        return new Store() {

            @Override
            public Socket<OutputStream> output() { return t.apply(Store.this.output()); }

            @Override
            public Socket<InputStream> input() { return t.unapply(Store.this.input()); }

            @Override
            public void delete() throws IOException { Store.this.delete(); }

            @Override
            public OptionalLong size() throws IOException { return Store.this.size(); }
        };
    }

    /**
     * Returns the content of this store.
     *
     * @throws IOException if co content is present or cannot be read for some reason.
     * @throws IllegalStateException if the content length exceeds {@link Integer#MAX_VALUE}.
     */
    default byte[] content() throws IOException {
        try {
            return input().map(DataInputStream::new).apply(in -> {
                @SuppressWarnings("ConstantConditions")
                final long length = size().getAsLong();
                if (length > Integer.MAX_VALUE) {
                    throw new IllegalStateException("Content length " + length + " exceeds Integer.MAX_VALUE.");
                }
                final byte[] content = new byte[(int) length];
                in.readFully(content);
                return content;
            });
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /** Sets the content of this store. */
    default void content(final byte[] content) throws IOException {
        try {
            acceptWriter(out -> out.write(content));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
