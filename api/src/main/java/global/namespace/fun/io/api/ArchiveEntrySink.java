/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

/**
 * An abstraction for writing the content of an underlying archive entry.
 *
 * @author Christian Schlichtherle
 */
public abstract class ArchiveEntrySink<E> extends ArchiveEntry<E> implements Sink {

    @Override
    public boolean canEqual(Object that) { return that instanceof ArchiveEntrySink; }
}
