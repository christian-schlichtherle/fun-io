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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.OptionalLong;

/**
 * @deprecated The {@link Buffer} interface is redundant since the introduction of {@link Store#deleteIfExists()} in
 *             Fun I/O 1.4.0.
 */
@Deprecated
public interface Buffer extends Store, Closeable {

    static Buffer of(Store s) {
        return new Buffer() {

            @Override
            public Socket<InputStream> input() {
                return s.input();
            }

            @Override
            public void acceptReader(XConsumer<? super InputStream> reader) throws Exception {
                s.acceptReader(reader);
            }

            @Override
            public <U> U applyReader(XFunction<? super InputStream, ? extends U> reader) throws Exception {
                return s.applyReader(reader);
            }

            @Override
            public Socket<OutputStream> output() {
                return s.output();
            }

            @Override
            public void acceptWriter(XConsumer<? super OutputStream> writer) throws Exception {
                s.acceptWriter(writer);
            }

            @Override
            public <U> U applyWriter(XFunction<? super OutputStream, ? extends U> writer) throws Exception {
                return s.applyWriter(writer);
            }

            @Override
            public void delete() throws IOException {
                s.delete();
            }

            @Override
            public OptionalLong size() throws IOException {
                return s.size();
            }

            @Override
            public boolean exists() throws IOException {
                return s.exists();
            }

            @Override
            public ConnectedCodec connect(Codec c) {
                return s.connect(c);
            }

            @Override
            public byte[] content() throws IOException {
                return s.content();
            }

            @Override
            public byte[] content(int max) throws IOException {
                return s.content(max);
            }

            @Override
            public void content(byte[] b) throws IOException {
                s.content(b);
            }

            @Override
            public void content(byte[] b, int off, int len) throws IOException {
                s.content(b, off, len);
            }
        };
    }

    /**
     * Returns a buffer which applies the given filter to the I/O streams loaned by this buffer.
     *
     * @param t the filter to apply to the I/O streams loaned by this buffer.
     */
    default Buffer map(Filter t) {
        return new Buffer() {

            @Override
            public Socket<InputStream> input() { return t.unapply(Buffer.this.input()); }

            @Override
            public Socket<OutputStream> output() { return t.apply(Buffer.this.output()); }

            @Override
            public void delete() throws IOException { Buffer.this.delete(); }

            @Override
            public OptionalLong size() throws IOException { return Buffer.this.size(); }
        };
    }

    /** Deletes the content of this buffer, if any. */
    default void close() throws IOException { deleteIfExists(); }
}
