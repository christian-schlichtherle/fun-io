/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

import java.io.Closeable;
import java.util.Optional;

/**
 * An abstraction for reading archive entries from an archive file.
 *
 * @see ArchiveFileOutput
 * @author Christian Schlichtherle
 */
public interface ArchiveFileInput<E> extends Iterable<ArchiveEntrySource<E>>, Closeable {

    /** Returns a source for reading the archive entry with the given name, if it exists. */
    Optional<ArchiveEntrySource<E>> source(String name);
}
