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
public abstract class ArchiveEntrySink implements Sink {

    /**
     * Copies the underlying archive entry in this archive file from the given archive entry source.
     */
    public abstract void copyFrom(ArchiveEntrySource source) throws Exception;
}
