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

import java.io.IOException;

final class Close {

    private Close() { }

    static void bothIO(final AutoCloseable first, final AutoCloseable second) throws IOException {
        try {
            both(first, second);
        } catch (IOException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }

    }

    private static void both(final AutoCloseable first, final AutoCloseable second) throws Exception {
        Throwable t1 = null;
        try {
            first.close();
        } catch (final Throwable t) {
            t1 = t;
            throw t;
        } finally {
            try {
                second.close();
            } catch (Throwable t) {
                if (null == t1) {
                    throw t;
                } else {
                    t1.addSuppressed(t);
                }
            }
        }
    }
}
