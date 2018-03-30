/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Source;
import global.namespace.fun.io.api.Store;

import javax.annotation.concurrent.Immutable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides message digest functions.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class MessageDigests {

    private MessageDigests() { }

    /** Returns a new SHA-1 message digest. */
    public static MessageDigest sha1() { return create("SHA-1"); }

    /**
     * Returns a new message digest for the given algorithm name.
     *
     * @param algorithm the algorithm name.
     * @throws IllegalArgumentException if no implementation of the algorithm
     *         is found.
     */
    public static MessageDigest create(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Returns a positive, big-endian integer in hexadecimal string notation
     * representing the value of the given message digest.
     * This is the canonical representation of message digests.
     *
     * @param digest a message digest.
     * @return a positive, big-endian integer in hexadecimal string notation
     *         representing the value of the message digest.
     */
    public static String valueOf(MessageDigest digest) {
        return new BigInteger(1, digest.digest()).toString(16);
    }

    /**
     * Updates the given message digest with the binary data from the given
     * source.
     *
     * @param digest the message digest to update.
     * @param source the source for reading the binary data.
     */
    public static void updateDigestFrom(final MessageDigest digest, final Source source) throws Exception {
        source.acceptReader(in -> {
            final byte[] buffer = new byte[Store.BUFSIZE];
            for (int read; 0 <= (read = in.read(buffer)); ) {
                digest.update(buffer, 0, read);
            }
        });
    }
}
