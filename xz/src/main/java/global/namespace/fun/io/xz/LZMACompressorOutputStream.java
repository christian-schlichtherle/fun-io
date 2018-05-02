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

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAOutputStream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

final class LZMACompressorOutputStream extends FilterOutputStream {

    LZMACompressorOutputStream(OutputStream out) throws IOException {
        super(new LZMAOutputStream(out, new LZMA2Options(), -1));
    }

    @Override
    public void flush() { }
}
