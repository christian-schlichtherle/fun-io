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
import global.namespace.fun.io.api.Loan;
import global.namespace.fun.io.api.Store;
import global.namespace.fun.io.api.Transformation;

import java.io.*;

final class BufferedInverseTransformation implements Transformation {

    private final Transformation transformation;
    private final Loan<Buffer> bufferLoan;

    BufferedInverseTransformation(final Transformation t, final Loan<Buffer> bl) {
        this.transformation = t;
        this.bufferLoan = bl;
    }

    @Override
    public Loan<OutputStream> apply(Loan<OutputStream> osl) {
        return osl.flatMap(originalOut -> bufferLoan.flatMap(buffer -> buffer.output().map(bufferOut -> decorate(bufferOut, () -> {
            if (buffer.exists()) { // for idempotence!
                try (OutputStream out = originalOut) {
                    transformation.unapply(buffer.input()).accept(in -> copy(in, out));
                } finally {
                    buffer.close();
                }
            }
        }))));
    }

    @Override
    public Loan<InputStream> unapply(Loan<InputStream> isl) {
        return isl.flatMap(originalIn -> bufferLoan.flatMap(buffer -> {
            try (InputStream in = originalIn) {
                transformation.apply(buffer.output()).accept(out -> copy(in, out));
            }
            return buffer.input().map(bufferIn -> decorate(bufferIn, buffer));
        }));
    }

    private static void copy(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[Store.BUFSIZE];
        for (int read; (read = in.read(buffer)) >= 0; ) {
            out.write(buffer, 0, read);
        }
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
    public Transformation inverse() { return transformation; }
}
