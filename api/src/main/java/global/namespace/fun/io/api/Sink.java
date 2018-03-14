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

import java.io.OutputStream;

/**
 * Emits output stream {@linkplain Socket sockets} in order to enable access to its stored content.
 *
 * @author Christian Schlichtherle
 */
public interface Sink {

    /** Returns an output stream socket for (over)writing the content of this sink. */
    Socket<OutputStream> output();

    /**
     * Returns a sink which applies the given transformation to the I/O streams loaned by this sink.
     *
     * @param t the transformation to apply to the I/O streams loaned by this sink.
     */
    default Sink map(Transformation t) { return () -> t.apply(output()); }
}
