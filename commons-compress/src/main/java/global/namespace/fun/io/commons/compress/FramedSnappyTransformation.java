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
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyDialect;

import java.io.InputStream;
import java.io.OutputStream;

final class FramedSnappyTransformation extends BufferedInvertibleTransformation {

    private final Parameters outputParameters;
    private final FramedSnappyDialect inputParameters;

    FramedSnappyTransformation(final Parameters outputParameters, final FramedSnappyDialect inputParameters) {
        this.outputParameters = outputParameters;
        this.inputParameters = inputParameters;
    }

    @Override
    public Socket<OutputStream> apply(Socket<OutputStream> output) {
        return output.map(os -> new FramedSnappyCompressorOutputStream(os, outputParameters));
    }

    @Override
    public Socket<InputStream> unapply(Socket<InputStream> input) {
        return input.map(is -> new FramedSnappyCompressorInputStream(is, inputParameters));
    }
}
