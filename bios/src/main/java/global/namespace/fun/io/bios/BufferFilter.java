package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.Filter;
import global.namespace.fun.io.api.Socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Christian Schlichtherle
 */
final class BufferFilter implements Filter {

    private final int size;

    BufferFilter(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size " + size + " is <= 0.");
        }
        this.size = size;
    }

    @Override
    public Socket<OutputStream> output(Socket<OutputStream> output) {
        return output.map(out -> new BufferedOutputStream(out, size));
    }

    @Override
    public Socket<InputStream> input(Socket<InputStream> input) {
        return input.map(in -> new BufferedInputStream(in, size));
    }
}
