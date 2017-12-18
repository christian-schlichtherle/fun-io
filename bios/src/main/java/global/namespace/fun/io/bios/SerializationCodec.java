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
import global.namespace.fun.io.api.Socket;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

final class SerializationCodec implements Codec {

    @Override
    public Encoder encoder(Socket<OutputStream> osl) {
        return obj -> osl.map(ObjectOutputStream::new).accept(oos -> oos.writeObject(obj));
    }

    @Override
    public Decoder decoder(final Socket<InputStream> isl) {
        return new Decoder() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T decode(Type expected) throws Exception {
                return isl.map(ObjectInputStream::new).apply(ois -> (T) ois.readObject());
            }
        };
    }
}
