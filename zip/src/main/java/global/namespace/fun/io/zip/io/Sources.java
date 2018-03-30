/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * Copyright (C) 2013 Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Source;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;

/**
 * Provides functions for {@link Source}s.
 *
 * @author Christian Schlichtherle (copied and edited from TrueLicense Core 2.3.1)
 */
@Immutable
public class Sources {

    @Deprecated
    public static <V> ExecuteStatement<V> execute(InputTask<V> task) { return new WithInputTask<>(task); }

    public interface ExecuteStatement<V> {
        V on(File file) throws Exception;
        V on(Source source) throws Exception;
    }

    private Sources() { }
}
