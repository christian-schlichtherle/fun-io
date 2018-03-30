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

    public static <V> ExecuteStatement<V> execute(ZipInputTask<V> task) { return new WithZipInputTask<V>(task); }

    public interface ExecuteStatement<V> {
        V on(File file) throws Exception;
        V on(ZipSource source) throws Exception;
    }

    private ZipSources() { }
}
