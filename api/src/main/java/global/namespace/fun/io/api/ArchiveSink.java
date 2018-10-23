/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

/**
 * An abstraction for safe access to some {@linkplain ArchiveOutputStream archive output stream}.
 *
 * @author Christian Schlichtherle
 */
@FunctionalInterface
public interface ArchiveSink extends GenSink<ArchiveOutputStream> {
}
