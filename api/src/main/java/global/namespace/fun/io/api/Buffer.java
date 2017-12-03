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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.OptionalLong;

public interface Buffer extends Store, Closeable {

    static Buffer of(Store s) {
        return new Buffer() {

            boolean closed;

            @Override
            public Socket<OutputStream> output() {
                check();
                return s.output();
            }

            @Override
            public Socket<InputStream> input() {
                check();
                return s.input();
            }

            @Override
            public void delete() throws IOException {
                check();
                s.delete();
            }

            @Override
            public OptionalLong size() throws IOException {
                check();
                return s.size();
            }

            private void check() {
                if (closed) {
                    throw new IllegalStateException("This buffer is already closed.");
                }
            }
        };
    }

    /** Deletes the content of this buffer, if any. */
    default void close() throws IOException {
        if (exists()) {
            delete();
        }
    }
}
