/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.api.function.XFunction;

/**
 * An abstraction for safe reading of archive entries from an archive without leaking resources.
 * An archive source provides a {@linkplain #input() socket} for safe access to an {@link ArchiveInput}.
 *
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface ArchiveSource {

    /**
     * Returns the underlying archive input socket for reading the archive entries.
     */
    Socket<ArchiveInput> input();

    /**
     * Loans an archive input from the underlying {@linkplain #input() socket} to the given consumer.
     * The archive input will be closed upon return from this method.
     */
    default void acceptReader(XConsumer<? super ArchiveInput> reader) throws Exception {
        input().accept(reader);
    }

    /**
     * Loans an archive input from the underlying {@linkplain #input() socket} to the given function and returns its
     * value.
     * The archive input will be closed upon return from this method.
     * <p>
     * It is an error to return the loaned archive input from the given function or any other object which holds on to
     * it.
     */
    default <U> U applyReader(XFunction<? super ArchiveInput, ? extends U> reader) throws Exception {
        return input().apply(reader);
    }
}
