/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.util.builder;

/**
 * An abstract builder.
 * Note that builders are <em>not</em> thread-safe.
 *
 * @param <P> The type of the parent builder, if defined.
 * @author Christian Schlichtherle
 */
public abstract class AbstractBuilder<P> {

    /**
     * Builds and returns a new product.
     * A call to this method does <em>not</em> alter the state of this builder,
     * so a client can continue to use it in order to build another product.
     */
    public abstract Object build();

    /**
     * Optional operation: Injects a new product into the parent builder,
     * if defined.
     *
     * @throws UnsupportedOperationException if this builder does not have a
     *         parent builder.
     */
    public P inject() {
        throw new UnsupportedOperationException(
                "This builder does not have a parent builder.");
    }
}
