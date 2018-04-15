/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import global.namespace.fun.io.api.Source;
import global.namespace.fun.io.api.Store;

import java.math.BigInteger;
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

    /**
     * Returns the digest value of the given source in canonical form.
     * The canonical form is a positive, big-endian integer in hexadecimal string notation representing the digest
     * value.
     */
    static String digestValueOf(final MessageDigest digest, final Source source) throws Exception {
        return source.applyReader(in -> {
            digest.reset();
            final byte[] buffer = new byte[Store.BUFSIZE];
            for (int read; 0 <= (read = in.read(buffer)); ) {
                digest.update(buffer, 0, read);
            }
            return new BigInteger(1, digest.digest()).toString(16);
        });
    }
}
