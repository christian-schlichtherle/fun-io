/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

/**
 * An abstraction for safe reading and writing of archive entries from and to an archive file without leaking resources.
 * An archive file store provides an {@linkplain #input() archive file input socket} and an
 * {@linkplain #output() archive file output socket}.
 *
 * @author Christian Schlichtherle
 */
public interface ArchiveStore extends ArchiveSource, ArchiveSink { }
