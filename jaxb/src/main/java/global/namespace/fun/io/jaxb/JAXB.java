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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import static java.util.Objects.requireNonNull;

/**
 * Provides static factory methods for a JAXB codec.
 *
 * @author Christian Schlichtherle
 */
public final class JAXB {

    private JAXB() { }

    /**
     * Uses {@link Marshaller}s and {@link Unmarshaller}s derived from the given {@link JAXBContext} to encode and
     * decode object graphs to and from octet streams.
     */
    public static Codec xmlCodec(JAXBContext c) { return new XMLCodec(requireNonNull(c)); }
}
