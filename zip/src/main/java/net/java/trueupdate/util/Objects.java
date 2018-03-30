/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.util;

import javax.annotation.Nullable;

/**
 * Provides static utility methods for use with {@link Object}s.
 *
 * @author Christian Schlichtherle (copied from TrueLicense Core 2.3.1)
 */
public final class Objects {

    public static <T> T nonNullOr(@Nullable T obj, T def) {
        return null != obj ? obj : def;
    }

    public static @Nullable <T> T nonDefaultOrNull(T obj, @Nullable T def) {
        return obj.equals(def) ? null : obj;
    }

    private Objects() { }
}
