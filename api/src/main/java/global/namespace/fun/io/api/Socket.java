/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.fun.io.api;

import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.api.function.XFunction;
import global.namespace.fun.io.api.function.XSupplier;

import java.util.Objects;

/**
 * A socket is a reusable object for safe and simple automatic resource management.
 * It loans {@linkplain AutoCloseable auto-closeable resources} of type {@code <T>} to
 * {@linkplain XConsumer consumers} or {@linkplain XFunction functions} and ensures that the resource gets
 * automatically {@linkplain AutoCloseable#close() closed} when the consumer or function terminates.
 * It can also transform resources by applying a {@linkplain XFunction function} while ensuring that the resource gets
 * closed if the function fails with an exception.
 * <p>
 * The canonical way to <em>create</em> a socket is to use a lambda expression.
 * The following example creates a socket which provides write access for appending bytes to the file {@code test.txt}:
 * <pre>{@code
 * File file = new File("test.txt");
 * Socket<FileOutputStream> foss = () -> new FileOutputStream(file, true);
 * }</pre>
 * The canonical way to <em>use</em> a socket is to provide a lambda expression for a consumer or function to its
 * {@link #accept(XConsumer)} or {@link #apply(XFunction)} methods.
 * The following example uses the preceding socket to append {@code "Hello world!"} to the file {@code test.txt}:
 * <pre>{@code
 * foss.accept(fos -> new PrintStream(fos).println("Hello world!"));
 * }</pre>
 * A socket can also get <em>transformed</em> in a fail-safe way by calling its {@link #map(XFunction)} or
 * {@link #flatMap(XFunction)} methods.
 * The following example first transforms the preceding file output stream socket into a print stream socket and then
 * appends {@code "Hello world!"} to the file {@code test.txt} again:
 * <pre>{@code
 * Socket<PrintStream> pss = foss.map(PrintStream::new);
 * pss.accept(ps -> ps.println("Hello world!"));
 * }</pre>
 * The preceding example can be simplified as follows:
 * <pre>{@code
 * foss.map(PrintStream::new).accept(ps -> ps.println("Hello world!"));
 * }</pre>
 * Because sockets are reusable {@code foss} and {@code pss} can be saved for subsequent use, including transformation:
 * On any use, a new {@code FileOutputStream} and a new {@code PrintStream} gets created.
 * The file output stream gets automatically closed in all examples, preventing the application from leaking file
 * descriptors.
 * Only the last two examples will close the print stream however.
 * With a print stream this is not a problem, but more complex decorators may buffer data or write some additional bytes
 * when closing, making it mandatory to close them, too.
 * Because of this, transforming a socket is generally preferable over decorating the given resource in a consumer or
 * function.
 * <p>
 * The following example safely transforms a file output stream socket for writing {@code "Hello world!"} to the
 * compressed text file {@code "test.txt.gz"}.
 * It then safely transforms a file input stream socket for reading the message back and printing it to standard output:
 * <pre>{@code
 * File file = new File("test.txt.gz");
 *
 * Socket<OutputStream> foss = () -> new FileOutputStream(file);
 * foss    .map(GZIPOutputStream::new)
 *         .map(PrintStream::new)
 *         .accept(ps -> ps.println("Hello world!"));
 *
 * Socket<InputStream> fiss = () -> new FileInputStream(file);
 * fiss    .map(GZIPInputStream::new)
 *         .map(InputStreamReader::new)
 *         .map(BufferedReader::new)
 *         .accept(br -> System.out.println(br.readLine()));
 * }</pre>
 * Should any transformation fail, e.g. because the file system is full or the file's content is not in GZIP format,
 * then the sockets will properly close the previously created output or input stream and no resources will be leaked.
 *
 * @see Transformation
 * @param <T> the type of the auto-closeable resource.
 * @author Christian Schlichtherle
 */
@SuppressWarnings({ "DeprecatedIsStillUsed", "deprecation" })
@FunctionalInterface
public interface Socket<T extends AutoCloseable> extends XSupplier<T> {

    /**
     * Returns a resource for direct access by the caller.
     * An implementation must either return a new auto-closeable resource upon each call or ignore any calls to its
     * {@link AutoCloseable#close()} method.
     *
     * @deprecated This method should not be used by clients because it would defeat the purpose of this interface.
     */
    @Deprecated
    T get() throws Exception;

    /**
     * Loans a resource to the given consumer.
     * The resource is obtained from a call to {@link #get()} and will be closed upon return from this method.
     */
    default void accept(final XConsumer<? super T> c) throws Exception {
        try (T resource = get()) {
            c.accept(resource);
        }
    }

    /**
     * Loans a resource to the given function and returns its value.
     * The resource is obtained from a call to {@link #get()} and will be closed upon return from this method.
     * <p>
     * It is an error to return the loaned resource from the given function or any other object which holds on to it.
     * Use the {@link #map(XFunction)} or {@link #flatMap(XFunction)} methods instead if you need to transform the
     * resource.
     */
    default <U> U apply(final XFunction<? super T, ? extends U> f) throws Exception {
        try (T resource = get()) {
            return f.apply(resource);
        }
    }

    /**
     * Returns a socket which applies the given function to the resources loaned by this socket.
     * If the given function fails then the resource gets closed before this method terminates, which makes the
     * transformation fail-safe.
     */
    default <U extends AutoCloseable> Socket<U> map(XFunction<? super T, ? extends U> f) {
        Objects.requireNonNull(f);
        return () -> {
            final T resource = get();
            try {
                return f.apply(resource);
            } catch (final Throwable t1) {
                try {
                    resource.close();
                } catch (Throwable t2) {
                    t1.addSuppressed(t2);
                }
                throw t1;
            }
        };
    }

    /**
     * Returns a socket which applies the given function to the resources loaned by this socket and gets its result.
     *
     * @see #map(XFunction)
     */
    default <U extends AutoCloseable> Socket<U> flatMap(XFunction<? super T, ? extends Socket<? extends U>> f) {
        return map(f.andThen(Socket::get));
    }
}
