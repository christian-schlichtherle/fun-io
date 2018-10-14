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
package global.namespace.fun.io.xz;

import global.namespace.fun.io.api.Filter;
import global.namespace.fun.io.api.Socket;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.LZMAOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

final class LZMAFilter implements Filter {

    @Override
    public Socket<OutputStream> output(Socket<OutputStream> output) {
        return output.map(LZMACompressorOutputStream::new);
    }

    @Override
    public Socket<InputStream> input(Socket<InputStream> input) {
        return input.map(LZMAInputStream::new);
    }
}
