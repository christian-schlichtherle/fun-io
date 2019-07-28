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
package global.namespace.fun.io.jaxb;

import global.namespace.fun.io.api.Codec;
import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.function.XConsumer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

final class XmlCodec implements Codec {

    private final JAXBContext context;
    private final XConsumer<Marshaller> marshallerModifier;
    private final XConsumer<Unmarshaller> unmarshallerModifier;

    XmlCodec(
            final JAXBContext c,
            final XConsumer<Marshaller> marshallerModifier,
            final XConsumer<Unmarshaller> unmarshallerModifier) {
        this.context = c;
        this.marshallerModifier = marshallerModifier;
        this.unmarshallerModifier = unmarshallerModifier;
    }

    @Override
    public Encoder encoder(Socket<OutputStream> output) {
        return obj -> output.accept(out -> {
            final Marshaller marshaller = context.createMarshaller();
            marshallerModifier.accept(marshaller);
            marshaller.marshal(obj, out);
        });
    }

    @Override
    public Decoder decoder(Socket<InputStream> input) {
        return new Decoder() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T decode(Type expected) throws Exception {
                return input.apply(in -> {
                    final Unmarshaller unmarshaller = context.createUnmarshaller();
                    unmarshallerModifier.accept(unmarshaller);
                    return (T) unmarshaller.unmarshal(in);
                });
            }
        };
    }
}
