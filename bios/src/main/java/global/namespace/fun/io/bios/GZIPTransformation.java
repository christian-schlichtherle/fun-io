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

import global.namespace.fun.io.api.Loan;
import global.namespace.fun.io.api.Store;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

final class GZIPTransformation extends BufferedInvertibleTransformation {

    @Override
    public Loan<OutputStream> apply(Loan<OutputStream> osl) {
        return osl.map(out -> new GZIPOutputStream(out, Store.BUFSIZE));
    }

    @Override
    public Loan<InputStream> unapply(Loan<InputStream> isl) {
        return isl.map(in -> new GZIPInputStream(in, Store.BUFSIZE));
    }
}
