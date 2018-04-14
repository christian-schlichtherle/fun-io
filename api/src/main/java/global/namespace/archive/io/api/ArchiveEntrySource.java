/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.archive.io.api;

import global.namespace.fun.io.api.Source;

/**
 * An abstraction for reading the content of an underlying archive entry.
 *
 * @author Christian Schlichtherle
 */
public abstract class ArchiveEntrySource<E> extends ArchiveEntry<E> implements Source {

    /** Copies all data of the underlying archive entry in this archive file to the given archive entry sink. */
    public abstract void copyTo(ArchiveEntrySink<?> sink) throws Exception;

    @Override
    public boolean canEqual(Object that) { return that instanceof ArchiveEntrySource; }
}
