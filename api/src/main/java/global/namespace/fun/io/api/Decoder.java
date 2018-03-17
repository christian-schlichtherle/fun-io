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
package global.namespace.fun.io.api;

import java.lang.reflect.Type;

/**
 * Decodes an object graph.
 *
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface Decoder {

    /**
     * Decodes an object graph of the expected type.
     *
     * @param  <T> the expected type of the decoded object.
     * @param  expected the expected type of the decoded object graph, e.g. {@code String.class}.
     *         This is just a hint and the implementation may ignore it.
     * @return A duplicate of the original object graph.
     *         Its actual type may differ from the expected type.
     */
    <T> T decode(Type expected) throws Exception;
}
