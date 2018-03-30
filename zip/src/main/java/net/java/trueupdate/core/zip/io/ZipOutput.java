/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.io;

import java.io.*;
import java.util.zip.*;

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
