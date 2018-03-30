/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.io;

import java.io.*;

/**
 * Provides functions for {@link ZipSource}s.
 *
 * @see ZipSinks
 * @author Christian Schlichtherle
 */
public class ZipSources {

    public static <V, X extends Exception>
            ExecuteStatement<V, X> execute(ZipInputTask<V, X> task) {
        return new WithZipInputTask<V, X>(task);
    }

    public interface ExecuteStatement<V, X extends Exception> {
        V on(File file) throws X, IOException;
        V on(ZipSource source) throws X, IOException;
    }

    private ZipSources() { }
}
