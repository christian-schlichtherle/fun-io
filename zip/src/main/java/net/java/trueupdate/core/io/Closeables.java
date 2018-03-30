package net.java.trueupdate.core.io;

import java.io.Closeable;
import java.io.IOException;
import javax.annotation.WillClose;

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
    public static <V, R extends Closeable, X extends Exception>
            V execute(final Task<V, R, X> task, final @WillClose R resource)
    throws X, IOException {
        X ex = null;
        try {
            return task.execute(resource);
        } catch (Exception ex2) {
            throw ex = (X) ex2;
        } finally {
            try {
                resource.close();
            } catch (IOException ex2) {
                if (null == ex) throw ex2;
            }
        }
    }

    private Closeables() { }
}
