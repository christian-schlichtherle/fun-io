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
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.Buffer;
import global.namespace.fun.io.api.Filter;
import global.namespace.fun.io.api.Socket;

import java.io.*;

import static global.namespace.fun.io.bios.BIOS.copy;

final class BufferedInverseFilter implements Filter {

    private final Filter filter;
    private final Socket<Buffer> bufferSocket;

    BufferedInverseFilter(final Filter t, final Socket<Buffer> bufferSocket) {
        this.filter = t;
        this.bufferSocket = bufferSocket;
    }

    @Override
    public Socket<OutputStream> apply(Socket<OutputStream> output) {
        return bufferSocket.flatMap(buffer -> {
            final AutoCloseable afterWritingBuffer = () -> {
                if (buffer.exists()) { // for idempotence!
                    try {
                        copy(filter.unapply(buffer.input()), output);
                    } finally {
                        buffer.close();
                    }
                }
            };
            return buffer.output().map(bufferOut -> decorate(bufferOut, afterWritingBuffer));
        });
    }

    @Override
    public Socket<InputStream> unapply(Socket<InputStream> input) {
        return bufferSocket.flatMap(buffer -> {
            copy(input, filter.apply(buffer.output()));
            return buffer.input().map(bufferIn -> decorate(bufferIn, buffer));
        });
    }

    private static OutputStream decorate(OutputStream out, AutoCloseable second) {
        return new FilterOutputStream(out) {
            public void close() throws IOException { Close.bothIO(out, second); }
        };
    }

    private static InputStream decorate(InputStream in, AutoCloseable second) {
        return new FilterInputStream(in) {
            public void close() throws IOException { Close.bothIO(in, second); }
        };
    }

    @Override
    public Filter inverse() { return filter; }
}
