package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.Socket;
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
    public Socket<OutputStream> apply(Socket<OutputStream> output) {
        return output.map(out -> new BufferedOutputStream(out, size));
    }

    @Override
    public Socket<InputStream> unapply(Socket<InputStream> input) { return input.map(in -> new BufferedInputStream(in, size)); }

    @Override
    public Transformation inverse() { return this; }
}
