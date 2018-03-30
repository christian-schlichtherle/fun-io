/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.util;

import javax.annotation.*;
import javax.annotation.concurrent.Immutable;

/**
 * Provides string functions.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public final class Strings {

    public static String requireNonEmpty(final String string) {
        if (string.isEmpty()) throw new IllegalArgumentException();
        return string;
    }

    public static @Nullable String nonEmptyOr(@CheckForNull String string, @Nullable String defaultValue) {
        return null != string && !string.isEmpty() ? string : defaultValue;
    }

    private Strings() { }
}
