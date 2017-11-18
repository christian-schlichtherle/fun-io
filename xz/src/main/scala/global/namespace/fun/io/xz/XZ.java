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
package global.namespace.fun.io.xz;

import global.namespace.fun.io.api.Transformation;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.LZMA2Options;

import static java.util.Objects.requireNonNull;

/**
 * Provides static factory methods for XZ transformations.
 *
 * @author Christian Schlichtherle
 */
public final class XZ {

    private XZ() { }

    /**
     * Returns a transformation which produces the LZMA2 compression format.
     * This method is equivalent to {@code lzma2(new LZMA2Options())}.
     */
    public static Transformation lzma2() { return xz(new LZMA2Options()); }

    /**
     * Returns a transformation which produces the XZ compression format.
     * This method is equivalent to {@code xz(o, org.tukaani.xz.XZ.CHECK_CRC64)}.
     */
    public static Transformation xz(FilterOptions o) { return xz(o, org.tukaani.xz.XZ.CHECK_CRC64); }

    /**
     * Returns a transformation which produces the XZ compression format.
     * This method is equivalent to {@code xz(new FilterOptions[] { o }, checkType)}.
     * <p>
     * This method does not check the integrity of the provided parameters.
     * Any error will only be detected when the transformed output stream loan gets used.
     *
     * @param checkType the type of the integrity check, e.g. @{code org.tukaani.xz.XZ.CHECK_CRC32}.
     */
    public static Transformation xz(FilterOptions o, int checkType) {
        return xz(new FilterOptions[] { requireNonNull(o) }, checkType);
    }

    /**
     * Returns a transformation which produces the XZ compression format.
     * This method is equivalent to {@code xz(o, org.tukaani.xz.XZ.CHECK_CRC64)}.
     * <p>
     * This method does not check the integrity of the provided parameter.
     * Any error will only be detected when the transformed output stream loan gets used.
     */
    public static Transformation xz(FilterOptions[] o) { return xz(o, org.tukaani.xz.XZ.CHECK_CRC64); }

    /**
     * Returns a transformation which produces the XZ compression format.
     * <p>
     * This method does not check the integrity of the provided parameters.
     * Any error will only be detected when the transformed output stream loan gets used.
     *
     * @param checkType the type of the integrity check, e.g. @{code org.tukaani.xz.XZ.CHECK_CRC32}.
     */
    public static Transformation xz(FilterOptions[] o, int checkType) { return new XZTransformation(o, checkType); }
}
