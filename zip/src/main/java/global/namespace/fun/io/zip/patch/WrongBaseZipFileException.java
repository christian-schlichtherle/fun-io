/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.patch;

import java.io.IOException;

/**
 * Indicates that the base ZIP file provided for patching doesn't match the base ZIP file when generating the patch ZIP
 * file.
 *
 * @author Christian Schlichtherle
 */
public final class WrongBaseZipFileException extends IOException {

    private static final long serialVersionUID = 0L;

    WrongBaseZipFileException(Throwable cause) { super(cause); }
}
