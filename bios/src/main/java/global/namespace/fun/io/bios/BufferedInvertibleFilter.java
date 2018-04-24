package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.Filter;

/** A filter which can invert itself by buffering the entire content on the heap - use with care!. */
public abstract class BufferedInvertibleFilter implements Filter {

    /** Returns the inverse of this filter by buffering the entire content on the heap - use with care! */
    @Override
    public final Filter inverse() { return BIOS.inverse(this); }
}
