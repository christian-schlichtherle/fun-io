/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

/**
 * An abstraction which adapts an underlying archive entry.
 *
 * @author Christian Schlichtherle
 */
public abstract class ArchiveEntry<E> {

    /** Returns the name of the underlying archive entry. */
    public abstract String name();

    /** Returns the size of the underlying archive entry. */
    public abstract long size();

    /** Returns {@code true} if and only if the underlying entry is a directory. */
    public abstract boolean isDirectory();

    /** Returns the underlying archive entry. */
    public abstract E entry();

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ArchiveEntry)) {
            return false;
        }
        final ArchiveEntry<?> that = (ArchiveEntry) obj;
        return that.canEqual(this) && this.entry().equals(that.entry());
    }

    protected abstract boolean canEqual(Object that);

    @Override
    public int hashCode() { return entry().hashCode(); }

    @Override
    public String toString() { return entry().toString(); }
}
