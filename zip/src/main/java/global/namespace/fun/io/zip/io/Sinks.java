/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * Copyright (C) 2013 Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.IOException;

/**
 * Provides functions for {@link Sink}s.
 *
 * @author Christian Schlichtherle (copied and edited from TrueLicense Core 2.3.1)
 */
@Immutable
public class Sinks {

    public static <V> ExecuteStatement<V> execute(OutputTask<V> task) { return new WithOutputTask<V>(task); }

    public interface ExecuteStatement<V> {
        V on(File file) throws Exception;
        V on(Sink sink) throws Exception;
    }

    private Sinks() { }
}
