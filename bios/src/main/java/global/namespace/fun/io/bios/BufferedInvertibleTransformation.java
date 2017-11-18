package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.Transformation;

/** A transformation which can invert itself by buffering the entire content on the heap - use with care!. */
public abstract class BufferedInvertibleTransformation implements Transformation {

    /** Returns the inverse of this transformation by buffering the entire content on the heap - use with care! */
    @Override
    public final Transformation inverse() { return BIOS.inverse(this); }
}
