/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.patch;

import java.io.IOException;

/**
 * Indicates that a computed {@link java.security.MessageDigest} did not match
 * an expected message digest.
 *
 * @author Christian Schlichtherle
 */
public final class WrongMessageDigestException extends IOException {

    private static final long serialVersionUID = 0L;

    WrongMessageDigestException(String message) { super(message); }
}
