/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides message digest functions.
 *
 * @author Christian Schlichtherle
 */
class MessageDigests {

    private MessageDigests() { }

    /** Returns a new SHA-1 message digest. */
    static MessageDigest sha1() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

}
