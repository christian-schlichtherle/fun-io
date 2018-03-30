/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * Copyright (C) 2013 Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.io;

import java.io.IOException;

/**
 * An abstraction for reading, writing and deleting binary data.
 *
 * @author Christian Schlichtherle (copied and edited from TrueLicense Core 2.3.1)
 */
public interface Store extends Source, Sink {

    /** A reasonable buffer size, which is {@value}. */
    int BUFSIZE = 8 * 1024;

    /** Deletes the binary data. */
    void delete() throws IOException;

    /** Returns {@code true} if and only if the binary data exists. */
    boolean exists();
}
