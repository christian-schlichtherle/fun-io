package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.Loan;
import global.namespace.fun.io.api.Transformation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

final class BufferedIOTransformation implements Transformation {

    private final int size;

    BufferedIOTransformation(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size " + size + " is <= 0.");
        }
        this.size = size;
    }

    @Override
    public Loan<OutputStream> apply(Loan<OutputStream> osl) {
        return osl.map(out -> new BufferedOutputStream(out, size));
    }

    @Override
    public Loan<InputStream> unapply(Loan<InputStream> isl) { return isl.map(in -> new BufferedInputStream(in, size)); }

    @Override
    public Transformation inverse() { return this; }
}
