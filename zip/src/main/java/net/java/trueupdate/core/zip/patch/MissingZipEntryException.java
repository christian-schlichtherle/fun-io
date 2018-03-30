/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.patch;

import java.io.IOException;

/**
 * Indicates that a ZIP entry is missing.
 *
 * @author Christian Schlichtherle
 */
public final class MissingZipEntryException extends IOException {

    private static final long serialVersionUID = 0L;

    MissingZipEntryException(String message) { super(message); }
}
