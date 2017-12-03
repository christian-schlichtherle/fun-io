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

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.Transformation;
import global.namespace.fun.io.api.function.XSupplier;

import javax.crypto.*;
import java.io.InputStream;
import java.io.OutputStream;

final class CipherTransformation implements Transformation {

    private final XSupplier<Cipher> outputCipherSupplier, inputCipherSupplier;

    CipherTransformation(final XSupplier<Cipher> outputCipherSupplier, final XSupplier<Cipher> inputCipherSupplier) {
        this.outputCipherSupplier = outputCipherSupplier;
        this.inputCipherSupplier = inputCipherSupplier;
    }

    @Override
    public Socket<OutputStream> apply(final Socket<OutputStream> oss) {
        return oss.map(out -> new CipherOutputStream(out, outputCipherSupplier.get()));
    }

    @Override
    public Socket<InputStream> unapply(final Socket<InputStream> iss) {
        return iss.map(in -> new CipherInputStream(in, inputCipherSupplier.get()));
    }

    @Override
    public Transformation inverse() { return new CipherTransformation(inputCipherSupplier, outputCipherSupplier); }
}
