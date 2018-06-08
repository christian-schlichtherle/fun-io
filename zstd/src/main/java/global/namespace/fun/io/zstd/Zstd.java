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
package global.namespace.fun.io.zstd;

import global.namespace.fun.io.api.Filter;

/**
 * This facade provides static factory methods for Zstd filters.
 *
 * @author Christian Schlichtherle
 */
public final class Zstd {

    private Zstd() { }

    /** Returns a filter which compresses/decompresses data using the Zstd format. */
    public static Filter zstd() { return zstd(3); }

    /** Returns a filter which compresses/decompresses data using the Zstd format with the given compression level. */
    public static Filter zstd(int level) { return new ZstdFilter(level); }
}
