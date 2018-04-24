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
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.bios.BufferedInvertibleFilter;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.Parameters;

import java.io.InputStream;
import java.io.OutputStream;

final class BlockLZ4Filter extends BufferedInvertibleFilter {

    private final Parameters parameters;

    BlockLZ4Filter(Parameters p) { this.parameters = p; }

    @Override
    public Socket<OutputStream> apply(Socket<OutputStream> output) {
        return output.map(os -> new BlockLZ4CompressorOutputStream(os, parameters));
    }

    @Override
    public Socket<InputStream> unapply(Socket<InputStream> input) { return input.map(BlockLZ4CompressorInputStream::new); }
}
