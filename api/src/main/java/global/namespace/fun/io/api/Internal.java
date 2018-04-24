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

import java.io.InputStream;
import java.io.OutputStream;

final class Internal {

    private Internal() { }

    static Filter compose(Filter first, Filter second) {
        return new Filter() {

            @Override
            public Socket<OutputStream> apply(Socket<OutputStream> output) { return second.apply(first.apply(output)); }

            @Override
            public Socket<InputStream> unapply(Socket<InputStream> input) { return second.unapply(first.unapply(input)); }

            @Override
            public Filter inverse() {
                final Filter composite = this;
                return new Filter() {

                    @Override
                    public Socket<OutputStream> apply(Socket<OutputStream> output) {
                        return first.inverse().apply(second.inverse().apply(output));
                    }

                    @Override
                    public Socket<InputStream> unapply(Socket<InputStream> input) {
                        return first.inverse().unapply(second.inverse().unapply(input));
                    }

                    @Override
                    public Filter inverse() { return composite; }
                };
            }
        };
    }

    static ConnectedCodec connect(Codec c, Store s) {
        return new ConnectedCodec() {
            @Override
            public Codec codec() { return c; }

            @Override
            public Store store() { return s; }
        };
    }
}
