/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api.archive;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.api.function.XFunction;

/**
 * An abstraction for safe reading of archive entries from an archive file without leaking resources.
 * An archive file source provides a {@linkplain #input() socket} for safe access to an {@link ArchiveFileInput}.
 *
 * @author Christian Schlichtherle
 */
public interface ArchiveFileSource<E> {

    /** Returns the underlying archive file input socket for reading the archive entries. */
    Socket<ArchiveFileInput<E>> input();

    /**
     * Loans an archive file input from the underlying {@linkplain #input() socket} to the given consumer.
     * The archive file input will be closed upon return from this method.
     */
    default void acceptReader(XConsumer<? super ArchiveFileInput<E>> reader) throws Exception {
        input().accept(reader);
    }

    /**
     * Loans an archive file input from the underlying {@linkplain #input() socket} to the given function
     * and returns its value.
     * The archive file input will be closed upon return from this method.
     * <p>
     * It is an error to return the loaned archive file input from the given function or any other object which holds
     * on to it.
     */
    default <U> U applyReader(XFunction<? super ArchiveFileInput<E>, ? extends U> reader) throws Exception {
        return input().apply(reader);
    }
}
