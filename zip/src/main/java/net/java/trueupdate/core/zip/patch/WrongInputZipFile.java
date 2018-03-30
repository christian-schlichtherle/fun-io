/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.patch;

import java.io.IOException;

/**
 * Indicates that the input ZIP file provided for patching doesn't match the
 * first ZIP file when generating the ZIP patch file.
 *
 * @author Christian Schlichtherle
 */
public final class WrongInputZipFile extends IOException {

    private static final long serialVersionUID = 0L;

    WrongInputZipFile(Throwable cause) { super(cause); }
}
