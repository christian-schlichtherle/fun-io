/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

import java.util.Locale;
import java.util.Objects;

/**
 * An abstraction for reading an archive entry.
 *
 * @author Christian Schlichtherle
 */
public abstract class ArchiveEntrySource implements Source {

    /**
     * Copies the underlying archive entry in this archive file to the given archive entry sink.
     */
    public final void copyTo(ArchiveEntrySink sink) throws Exception {
        sink.copyFrom(this);
    }

    /**
     * Returns the name of the archive entry.
     */
    public abstract String name();

    /**
     * Returns {@code true} if and only if the archive entry represents a directory.
     */
    public abstract boolean directory();

    /**
     * Returns the size of the archive entry.
     */
    public abstract long size();

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ArchiveEntrySource)) {
            return false;
        }
        final ArchiveEntrySource that = (ArchiveEntrySource) obj;
        return that.canEqual(this) &&
                this.name().equals(that.name()) &&
                this.directory() == that.directory() &&
                this.size() == that.size();
    }

    protected boolean canEqual(Object that) {
        return that instanceof ArchiveEntrySource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), directory(), size());
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "%s(name=%s, directory=%b, size=%d",
                getClass().getName(), name(), directory(), size());
    }
}
