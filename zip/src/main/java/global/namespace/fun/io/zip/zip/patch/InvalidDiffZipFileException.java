/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.patch;

import java.io.IOException;

/**
 * Indicates that the integrity of the ZIP patch file has been violated.
 *
 * @author Christian Schlichtherle
 */
public final class InvalidDiffZipFileException extends IOException {

    private static final long serialVersionUID = 0L;

    InvalidDiffZipFileException(Throwable cause) {
        super(cause);
    }
}
