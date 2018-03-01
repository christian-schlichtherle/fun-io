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
import global.namespace.fun.io.bios.BufferedInvertibleTransformation;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream.MAX_BLOCKSIZE;
import static org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream.MIN_BLOCKSIZE;

final class BZIP2Transformation extends BufferedInvertibleTransformation {

    private final int blockSize;

    BZIP2Transformation(final int blockSize) {
        if (blockSize < MIN_BLOCKSIZE || MAX_BLOCKSIZE < blockSize) {
            throw new IllegalArgumentException("Block size " + blockSize + " is out of range from " + MIN_BLOCKSIZE + " to " + MAX_BLOCKSIZE + ".");
        }
        this.blockSize = blockSize;
    }

    @Override
    public Socket<OutputStream> apply(Socket<OutputStream> output) {
        return output.map(out -> new BZip2CompressorOutputStream(out, blockSize));
    }

    @Override
    public Socket<InputStream> unapply(Socket<InputStream> input) { return input.map(BZip2CompressorInputStream::new); }
}
