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

    public static <V, X extends Exception>
            ExecuteStatement<V, X> execute(OutputTask<V, X> task) {
        return new WithOutputTask<V, X>(task);
    }

    public interface ExecuteStatement<V, X extends Exception> {
        V on(File file) throws X, IOException;
        V on(Sink sink) throws X, IOException;
    }

    private Sinks() { }
}
