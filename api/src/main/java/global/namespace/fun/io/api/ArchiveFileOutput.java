/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

import java.io.Closeable;

/**
 * An abstraction for writing archive entries to an archive file.
 *
 * @see ArchiveFileInput
 * @author Christian Schlichtherle
 */
public interface ArchiveFileOutput<E> extends Closeable {

    /** Returns {@code true} if and only if this is a JAR file. */
    boolean isJar();

    /** Returns a sink for writing the archive entry with the given name. */
    ArchiveEntrySink<E> sink(String name);
}
