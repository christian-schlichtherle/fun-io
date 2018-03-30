/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The {@link ZipOutputStream} interface as it should have been.
 *
 * @see ZipInput
 * @author Christian Schlichtherle
 */
public interface ZipOutput extends Closeable {

    /** Returns a <em>new</em> ZIP entry. */
    ZipEntry entry(String name);

    /** Returns an output stream for writing the ZIP entry contents. */
    OutputStream stream(ZipEntry entry) throws IOException;
}
