/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.patch;

import java.io.IOException;

/**
 * Indicates that the integrity of the patch ZIP file has been violated.
 *
 * @author Christian Schlichtherle
 */
public final class InvalidPatchZipFileException extends IOException {

    private static final long serialVersionUID = 0L;

    InvalidPatchZipFileException(Throwable cause) { super(cause); }
}
