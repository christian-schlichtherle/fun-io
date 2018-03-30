/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.io;

import edu.umd.cs.findbugs.annotations.CreatesObligation;
import java.io.IOException;

/**
 * An abstraction for reading ZIP files.
 *
 * @see    ZipSink
 * @author Christian Schlichtherle
 */
public interface ZipSource {

    /** Returns a new ZIP input for reading its entries. */
    @CreatesObligation ZipInput input() throws IOException;
}
