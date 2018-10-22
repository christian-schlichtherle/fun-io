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
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.*;
import global.namespace.fun.io.api.function.XFunction;
import global.namespace.fun.io.api.function.XSupplier;
import global.namespace.fun.io.spi.Copy;
import global.namespace.fun.io.spi.UncloseableInputStream;
import global.namespace.fun.io.spi.UncloseableOutputStream;

import javax.crypto.Cipher;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;
import java.util.jar.JarOutputStream;
import java.util.prefs.Preferences;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static global.namespace.fun.io.api.Store.BUFSIZE;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Objects.requireNonNull;

/**
 * This facade provides static factory methods for codecs, filters, sockets, stores, archive file stores et al.
 * It depends on the Java Runtime Environment (JRE) only.
 * The abbreviation stands for Basic Input/Output System (pun intended).
 *
 * @author Christian Schlichtherle
 */
public final class BIOS {

    private BIOS() { }

      //////////////////////////
     ///////// CODECS /////////
    //////////////////////////

    /**
     * Uses {@link ObjectOutputStream}s and {@link ObjectInputStream}s to encode and decode object graphs to and from
     * octet streams.
     */
    public static Codec serialization() { return new SerializationCodec(); }

    /**
     * Uses new {@link XMLEncoder}s and {@link XMLDecoder}s to encode and decode object graphs to and from octet
     * streams.
     */
    public static Codec xml() { return xml(XMLEncoder::new, XMLDecoder::new); }

    /**
     * Uses new {@link XMLEncoder}s and {@link XMLDecoder}s obtained by the given functions in order to encode and
     * decode object graphs to and from octet streams.
     */
    public static Codec xml(XFunction<? super OutputStream, ? extends XMLEncoder> xmlEncoders,
                            XFunction<? super InputStream, ? extends XMLDecoder> xmlDecoders) {
        return new XMLCodec(requireNonNull(xmlEncoders), requireNonNull(xmlDecoders));
    }

      ///////////////////////////
     ///////// FILTERS /////////
    ///////////////////////////

    /** Returns a filter which encodes/decodes data using Base64. */
    public static Filter base64() { return base64(Base64.getEncoder(), Base64.getDecoder()); }

    /** Returns a filter which encodes/decodes data using the given Base64 encoder and decoder. */
    public static Filter base64(Base64.Encoder e, Base64.Decoder d) {
        return new Base64Filter(requireNonNull(e), requireNonNull(d));
    }

    /** Returns a filter which buffers I/O using a buffer size of {@value Store#BUFSIZE} bytes. */
    public static Filter buffer() { return buffer(Store.BUFSIZE); }

    /** Returns a filter which buffers I/O using the given buffer size in bytes. */
    public static Filter buffer(int size) { return new BufferedIOFilter(size); }

    /**
     * Returns a filter which encrypts/decrypts data using the given cipher suppliers for output and input.
     *
     * @param ciphers a function which returns an initialized cipher.
     *                If its input parameter is {@code false}, then the returned cipher must be initialized for input,
     *                otherwise it must be initialized for output.
     */
    public static Filter cipher(XFunction<? super Boolean, ? extends Cipher> ciphers) {
        requireNonNull(ciphers);
        return new CipherFilter(() -> ciphers.apply(false), () -> ciphers.apply(true));
    }

    /**
     * Returns a filter which compresses/decompresses data using a ZIP deflater/inflater with the default compression
     * level.
     */
    public static Filter deflate() { return deflate(Deflater.DEFAULT_COMPRESSION); }

    /**
     * Returns a filter which compresses/decompresses data using a ZIP deflater/inflater with the given compression
     * level.
     *
     * @see Deflater
     */
    public static Filter deflate(int level) {
        if (level < Deflater.DEFAULT_COMPRESSION || Deflater.BEST_COMPRESSION < level) {
            throw new IllegalArgumentException(level + " is not in the range from " + Deflater.DEFAULT_COMPRESSION + " to " + Deflater.BEST_COMPRESSION + ".");
        }
        return deflate(() -> new Deflater(level), Inflater::new);
    }

    /** Returns a filter which compresses/decompresses data using a ZIP deflater/inflater. */
    public static Filter deflate(XSupplier<Deflater> deflaterSupplier, XSupplier<Inflater> inflaterSupplier) {
        return new DeflateFilter(requireNonNull(deflaterSupplier), requireNonNull(inflaterSupplier));
    }

    /** Returns a filter which compresses/decompresses data using the GZIP format. */
    public static Filter gzip() { return new GZIPFilter(); }

    /** Returns the identity filter. */
    public static Filter identity() { return Filter.IDENTITY; }

    /**
     * Returns a filter which decompresses/compresses data using a ZIP inflater/deflater with the default comporession
     * level.
     */
    public static Filter inflate() { return inflate(Deflater.DEFAULT_COMPRESSION); }

    /**
     * Returns a filter which decompresses/compresses data using a ZIP inflater/deflater with the given compression
     * level.
     */
    public static Filter inflate(int level) {
        if (level < Deflater.DEFAULT_COMPRESSION || Deflater.BEST_COMPRESSION < level) {
            throw new IllegalArgumentException(level + " is not in the range from " + Deflater.DEFAULT_COMPRESSION + " to " + Deflater.BEST_COMPRESSION + ".");
        }
        return inflate(Inflater::new, () -> new Deflater(level));
    }

    /** Returns a filter which decompresses/compresses data using a ZIP inflater/deflater. */
    public static Filter inflate(XSupplier<Inflater> inflaterSupplier, XSupplier<Deflater> deflaterSupplier) {
        return new InflateFilter(requireNonNull(inflaterSupplier), requireNonNull(deflaterSupplier));
    }

      ///////////////////////////
     ///////// SOURCES /////////
    ///////////////////////////

    /**
     * Returns a source which loads the resource with the given {@code name} using the current thread's
     * {@linkplain Thread#getContextClassLoader() context class loader}.
     *
     * @param  name the name of the resource to load.
     */
    public static Source resource(String name) {
        return resource(name, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Returns a source which loads the resource with the given {@code name} using the given nullable class loader.
     *
     * @param  name the name of the resource to load.
     * @param  cl
     *         The nullable class loader to use for loading the resource.
     *         If this is {@code null}, then the system class loader is used.
     */
    public static Source resource(String name, ClassLoader cl) {
        return () -> () -> Optional
                .ofNullable(null == cl ? ClassLoader.getSystemResourceAsStream(name) : cl.getResourceAsStream(name))
                .orElseThrow(() -> new FileNotFoundException(name));
    }

    /**
     * Returns a source which reads from standard input without ever closing it.
     *
     * @see #stream(InputStream)
     */
    public static Source stdin() { return stream(System.in); }

    /**
     * Returns a source which will never {@linkplain InputStream#close() close} the given input stream.
     * This is intended to be used for data streaming or for interoperability with other libraries and frameworks.
     */
    public static Source stream(InputStream in) {
        requireNonNull(in);
        return () -> () -> new UncloseableInputStream(in);
    }

    /** Returns a source which reads the content of the given URL. */
    public static Source url(URL url) {
        requireNonNull(url);
        return () -> url::openStream;
    }

      /////////////////////////
     ///////// SINKS /////////
    /////////////////////////

    /**
     * Returns a sink which writes to standard error without ever closing it.
     *
     * @see #stream(OutputStream)
     */
    public static Sink stderr() { return stream(System.err); }

    /**
     * Returns a sink which writes to standard output without ever closing it.
     *
     * @see #stream(OutputStream)
     */
    public static Sink stdout() { return stream(System.out); }

    /**
     * Returns a sink which will never {@linkplain OutputStream#close() close} the given output stream.
     * This is intended to be used for data streaming or for interoperability with other libraries and frameworks.
     * Upon a call to the {@code close()} method on the loaned output stream, the {@code flush()} method gets called on
     * the given output stream.
     */
    public static Sink stream(OutputStream out) {
        requireNonNull(out);
        return () -> () -> new UncloseableOutputStream(out);
    }

      //////////////////////////
     ///////// STORES /////////
    //////////////////////////

    /** Returns a store for the file referenced by the given path. */
    public static FileStore file(File path) { return file(path, false); }

    /** Returns a store for the file referenced by the given path, potentially appending to it if {@code append} is {@code true}. */
    public static FileStore file(final File path, final boolean append) {
        final FileStore store = file(path.toPath());
        return append ? store.onOutput(APPEND, CREATE) : store;
    }

    /** Returns a store for the file referenced by the given path. */
    public static FileStore file(Path path) { return new RealPathStore(requireNonNull(path)); }

    /** Returns a store for the file referenced by the given path. */
    public static FileStore file(String path) { return file(path, false); }

    /**
     * Returns a store for the file referenced by the given path, potentially appending to it if {@code append} is
     * {@code true}.
     */
    public static FileStore file(final String path, final boolean append) {
        final FileStore store = file(Paths.get(path));
        return append ? store.onOutput(APPEND, CREATE) : store;
    }

    /** Returns a new in-memory store with the default buffer size. */
    public static Store memory() { return memory(BUFSIZE); }

    /** Returns a new in-memory store with the given buffer size. */
    public static Store memory(int bufferSize) { return new MemoryStore(bufferSize); }

    /** Returns a store for the given path. */
    public static PathStore path(Path p) { return file(p); }

    /** Returns a store for the given preferences node and key. */
    public static Store preferences(Preferences p, String key) {
        return new PreferencesStore(requireNonNull(p), requireNonNull(key));
    }

    /** Returns a store for the system preferences node for the package of the given class and the given key. */
    public static Store systemPreferences(Class<?> classInPackage, String key) {
        return preferences(Preferences.systemNodeForPackage(classInPackage), key);
    }

    /** Returns a store for the user preferences node for the package of the given class and the given key. */
    public static Store userPreferences(Class<?> classInPackage, String key) {
        return preferences(Preferences.userNodeForPackage(classInPackage), key);
    }

    /** A store which allows to switch open options for input and output. */
    public interface FileStore extends PathStore {

        /** Returns a new file store which uses the given open options on input. */
        FileStore onInput(OpenOption... options);

        /** Returns a new file store which uses the given open options on output. */
        FileStore onOutput(OpenOption... options);
    }

    /** A store which allows to switch open options for input and output. */
    public interface PathStore extends Store {

        /** Returns a new path store which uses the given open options on input. */
        PathStore onInput(OpenOption... options);

        /** Returns a new path store which uses the given open options on output. */
        PathStore onOutput(OpenOption... options);
    }

      //////////////////////////////////
     ///////// ARCHIVE STORES /////////
    //////////////////////////////////

    /** Returns an archive store for transparent read/write access to the directory referenced by the given path. */
    public static ArchiveStore directory(File path) { return directory(path.toPath()); }

    /** Returns an archive store for transparent read/write access to the directory referenced by the given path. */
    public static ArchiveStore directory(Path path) { return new DirectoryStore(requireNonNull(path)); }

    /** Returns an archive store for transparent read/write access to the directory referenced by the given path. */
    public static ArchiveStore directory(String path) { return directory(Paths.get(path)); }

    /** Returns an archive store for read/write access to the JAR file referenced by the given path. */
    public static ArchiveStore jar(final File path) {
        requireNonNull(path);
        return new ArchiveStore() {

            @Override
            public Socket<ArchiveInput> input() { return () -> new ZipFileAdapter(new ZipFile(path)); }

            @Override
            public Socket<ArchiveOutput> output() {
                return () -> new JarOutputStreamAdapter(new JarOutputStream(new FileOutputStream(path)));
            }
        };
    }

    /** Returns an archive store for read/write access to the JAR file referenced by the given path. */
    public static ArchiveStore jar(String path) { return jar(new File(path)); }

    /** Returns an archive store for read/write access to the ZIP file referenced by the given path. */
    public static ArchiveStore zip(final File path) {
        requireNonNull(path);
        return new ArchiveStore() {

            @Override
            public Socket<ArchiveInput> input() { return () -> new ZipFileAdapter(new ZipFile(path)); }

            @Override
            public Socket<ArchiveOutput> output() {
                return () -> new ZipOutputStreamAdapter(new ZipOutputStream(new FileOutputStream(path)));
            }
        };
    }

    /** Returns an archive store for read/write access to the ZIP file referenced by the given path. */
    public static ArchiveStore zip(String path) { return zip(new File(path)); }

      /////////////////////////////
     ///////// UTILITIES /////////
    /////////////////////////////

    /**
     * Copies the entries from the given archive source to the given archive sink.
     * <p>
     * This is a high performance implementation which uses a pooled background thread to fill a FIFO of pooled buffers
     * which is concurrently flushed by the current thread.
     * It performs best when used with <em>unbuffered</em> streams.
     *
     * @param source the archive source to read the entries from.
     * @param sink the archive sink to write the entries to.
     */
    public static void copy(ArchiveSource source, ArchiveSink sink) throws Exception { Copy.copy(source, sink); }

    /**
     * Copies the data from the given source to the given sink.
     * <p>
     * This is a high performance implementation which uses a pooled background thread to fill a FIFO of pooled buffers
     * which is concurrently flushed by the current thread.
     * It performs best when used with <em>unbuffered</em> streams.
     *
     * @param source the source for reading the data from.
     * @param sink the sink for writing the data to.
     */
    public static void copy(Source source, Sink sink) throws Exception { Copy.copy(source, sink); }

    /**
     * Copies the data from the given input stream socket to the given output stream socket.
     * <p>
     * This is a high performance implementation which uses a pooled background thread to fill a FIFO of pooled buffers
     * which is concurrently flushed by the current thread.
     * It performs best when used with <em>unbuffered</em> streams.
     *
     * @param input the input stream socket for reading the data from.
     * @param output the output stream socket for writing the data to.
     */
    public static void copy(Socket<? extends InputStream> input, Socket<? extends OutputStream> output) throws Exception {
        Copy.copy(input, output);
    }

    /**
     * Returns a deep clone of the given object by serializing it to a memory store and de-serializing it again.
     * The memory store uses {@value Store#BUFSIZE} bytes as its initial buffer size.
     *
     * @see #serialization()
     * @see #memory()
     */
    public static <T extends Serializable> T clone(T t) throws Exception { return clone(t, Store.BUFSIZE); }

    /**
     * Returns a deep clone of the given object by serializing it to a memory store and de-serializing it again.
     * The memory store uses the given number of byes as its initial buffer size.
     *
     * @see #serialization()
     * @see #memory(int)
     */
    public static <T extends Serializable> T clone(T t, int bufferSize) throws Exception {
        return serialization().connect(memory(bufferSize)).clone(t);
    }

    /**
     * Returns the content of the given source.
     *
     * @throws ContentTooLargeException if the content exceeds {@link Integer#MAX_VALUE} bytes.
     * @throws IOException if there is no content or if the content cannot be read for some reason.
     */
    public static byte[] content(Source source) throws Exception { return content(source, Integer.MAX_VALUE); }

    /**
     * Returns the content of the given source.
     *
     * @throws IllegalArgumentException if {@code max} is less than zero.
     * @throws ContentTooLargeException if the content exceeds {@code max } bytes.
     * @throws IOException if there is no content or if the content cannot be read for some reason.
     */
    public static byte[] content(final Source source, final int max) throws Exception {
        if (source instanceof Store) {
            return ((Store) source).content(max);
        } else {
            final Store memory = memory();
            copy(source, memory);
            return memory.content(max);
        }
    }
}
