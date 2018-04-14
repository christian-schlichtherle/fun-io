/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import java.io.IOException;

/**
 * Indicates that a computed {@link java.security.MessageDigest} did not match an expected message digest.
 *
 * @author Christian Schlichtherle
 */
public class WrongMessageDigestException extends IOException {

    private static final long serialVersionUID = 0L;

    WrongMessageDigestException(String message) { super(message); }
}
