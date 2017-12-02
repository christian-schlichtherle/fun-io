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

import global.namespace.fun.io.api.Codec;
import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Loan;

import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

final class JAXBCodec implements Codec {

    private final JAXBContext context;

    JAXBCodec(final JAXBContext c) { this.context = c; }

    @Override
    public Encoder encoder(Loan<OutputStream> osl) {
        return obj -> osl.accept(out -> context.createMarshaller().marshal(obj, out));
    }

    @Override
    public Decoder decoder(Loan<InputStream> isl) {
        return new Decoder() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T decode(Type expected) throws Exception {
                return isl.apply(in -> (T) context.createUnmarshaller().unmarshal(in));
            }
        };
    }
}