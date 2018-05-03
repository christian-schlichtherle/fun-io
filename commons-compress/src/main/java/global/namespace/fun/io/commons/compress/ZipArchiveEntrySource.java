/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import java.io.InputStream;

/**
 * Supports writing the content of an underlying ZIP archive entry.
 *
 * @author Christian Schlichtherle
 */
abstract class ZipArchiveEntrySource extends ArchiveEntrySource<ZipArchiveEntry> {

    abstract Socket<InputStream> rawInput();
}
