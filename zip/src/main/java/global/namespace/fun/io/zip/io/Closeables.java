package global.namespace.fun.io.zip.io;

import javax.annotation.WillClose;
import java.io.Closeable;
import java.io.IOException;

/**
 * Provides functions for {@link Closeable}s.
 *
 * @author Christian Schlichtherle
 */
public final class Closeables {

    /**
     * Executes the given task on the given resource and finally closes it.
     * If both {@link Task#execute} and {@link Closeable#close} throw an
     * exception, then only the exception thrown by the task prevails.
     */
    @SuppressWarnings("unchecked")
    public static <V, R extends AutoCloseable> V execute(final Task<V, R> task, final @WillClose R resource)
    throws Exception {
        Exception ex = null;
        try {
            return task.execute(resource);
        } catch (Exception ex2) {
            throw ex = ex2;
        } finally {
            try {
                resource.close();
            } catch (Exception ex2) {
                if (null == ex) {
                    throw ex2;
                }
            }
        }
    }

    private Closeables() { }
}
