/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Socket;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The {@link ZipFile} interface as it should have been.
 *
 * @see ZipOutput
 * @author Christian Schlichtherle
 */
public interface ZipInput extends Iterable<ZipEntry>, Closeable {

    /** Looks up the nullable ZIP entry with the given name. */
    Optional<ZipEntry> entry(String name);

    /** Returns an input stream for reading the ZIP entry contents. */
    Socket<InputStream> input(ZipEntry entry);
}
