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

    public static <V> ExecuteStatement<V> execute(ZipOutputTask<V> task) { return new WithZipOutputTask<V>(task); }

    public interface ExecuteStatement<V> {
        V on(File file) throws Exception;
        V on(ZipSink sink) throws Exception;
    }

    private ZipSinks() { }
}
