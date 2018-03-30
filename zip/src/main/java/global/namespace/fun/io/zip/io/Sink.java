/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * Copyright (C) 2013 Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import edu.umd.cs.findbugs.annotations.CreatesObligation;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An abstraction for writing binary data.
 *
 * @see    Source
 * @author Christian Schlichtherle (copied and edited from TrueLicense Core 2.3.1)
 */
public interface Sink {

    /**
     * Returns a new output stream for writing the binary data to this sink.
     */
    @CreatesObligation OutputStream output() throws Exception;
}
