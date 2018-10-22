/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

import java.util.Locale;
import java.util.Objects;

/**
 * An abstraction which adapts an underlying archive entry.
 *
 * @author Christian Schlichtherle
 */
public abstract class ArchiveEntry<E> {

    /**
     * Returns the name of the underlying archive entry.
     */
    public abstract String name();

    /**
     * Returns the size of the underlying archive entry.
     */
    public abstract long size();

    /**
     * Returns {@code true} if and only if the underlying entry is a directory.
     */
    public abstract boolean isDirectory();

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ArchiveEntry)) {
            return false;
        }
        final ArchiveEntry<?> that = (ArchiveEntry) obj;
        return that.canEqual(this) &&
                this.name().equals(that.name()) &&
                this.size() == that.size() &&
                this.isDirectory() == that.isDirectory();
    }

    protected abstract boolean canEqual(Object that);

    @Override
    public int hashCode() {
        return Objects.hash(name(), size(), isDirectory());
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "%s(name=%s, size=%d, isDirectory=%b",
                getClass().getName(), name(), size(), isDirectory());
    }
}
