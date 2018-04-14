/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.archive.io.delta;

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
     * Returns a positive, big-endian integer in hexadecimal string notation representing the value of the given message
     * digest.
     * This is the canonical representation of message digests.
     *
     * @param  digest a message digest.
     * @return a positive, big-endian integer in hexadecimal string notation representing the value of the message
     *         digest.
     */
    static String valueOf(MessageDigest digest) {
        return new BigInteger(1, digest.digest()).toString(16);
    }

    /**
     * Updates the given message digest with the binary data from the given source.
     *
     * @param digest the message digest to to.
     * @param source the source for reading the binary data.
     */
    static void updateDigestFrom(final MessageDigest digest, final Source source) throws Exception {
        source.acceptReader(in -> {
            final byte[] buffer = new byte[Store.BUFSIZE];
            for (int read; 0 <= (read = in.read(buffer)); ) {
                digest.update(buffer, 0, read);
            }
        });
    }
}
