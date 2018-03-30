/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import java.io.File;
import java.io.IOException;

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
