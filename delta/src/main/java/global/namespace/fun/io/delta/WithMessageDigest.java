/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import global.namespace.fun.io.api.Source;
import global.namespace.fun.io.api.Store;

import java.math.BigInteger;
import java.security.MessageDigest;

/** @author Christian Schlichtherle */
interface WithMessageDigest {

    static WithMessageDigest of(MessageDigest digest) { return () -> digest; }

    /** Returns the message digest. */
    MessageDigest digest();

    /**
     * Returns the digest value of the given source in canonical form.
     * The canonical form is a positive, big-endian integer in hexadecimal string notation representing the digest
     * value.
     * This method modifies the state of the {@linkplain #digest message digest}, so it's not thread safe.
     */
    default String digestValueOf(final Source source) throws Exception {
        return source.applyReader(in -> {
            final MessageDigest digest = digest();
            digest.reset();
            final byte[] buffer = new byte[Store.BUFSIZE];
            for (int read; 0 <= (read = in.read(buffer)); ) {
                digest.update(buffer, 0, read);
            }
            return new BigInteger(1, digest.digest()).toString(16);
        });
    }
}
