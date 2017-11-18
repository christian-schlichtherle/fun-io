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

import global.namespace.fun.io.api.Loan;
import global.namespace.fun.io.bios.BufferedInvertibleTransformation;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

final class LZMATransformation extends BufferedInvertibleTransformation {

    @Override
    public Loan<OutputStream> apply(Loan<OutputStream> osl) { return osl.map(LZMACompressorOutputStream::new); }

    @Override
    public Loan<InputStream> unapply(Loan<InputStream> isl) { return isl.map(LZMACompressorInputStream::new); }
}
