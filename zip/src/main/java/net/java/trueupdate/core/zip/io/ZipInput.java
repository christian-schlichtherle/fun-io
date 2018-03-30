/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;

/**
 * The {@link ZipFile} interface as it should have been.
 *
 * @see ZipOutput
 * @author Christian Schlichtherle
 */
public interface ZipInput extends Iterable<ZipEntry>, Closeable {

    /** Looks up the nullable ZIP entry with the given name. */
    @Nullable ZipEntry entry(String name);

    /** Returns an input stream for reading the ZIP entry contents. */
    InputStream stream(ZipEntry entry) throws IOException;
}
