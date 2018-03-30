/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import java.io.File;
import java.io.IOException;

/**
 * Provides functions for {@link ZipSink}s.
 *
 * @see ZipSources
 * @author Christian Schlichtherle
 */
public class ZipSinks {

    public static <V, X extends Exception>
            ExecuteStatement<V, X> execute(ZipOutputTask<V, X> task) {
        return new WithZipOutputTask<V, X>(task);
    }

    public interface ExecuteStatement<V, X extends Exception> {
        V on(File file) throws X, IOException;
        V on(ZipSink sink) throws X, IOException;
    }

    private ZipSinks() { }
}
