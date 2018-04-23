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

import javax.crypto.Cipher;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.jar.JarOutputStream;
import java.util.prefs.Preferences;
import java.util.zip.*;

import static global.namespace.fun.io.api.Store.BUFSIZE;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Objects.requireNonNull;

/**
 * This facade provides static factory methods for codecs, transformations, sockets, stores, archive file stores et al.
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

      ///////////////////////////////////
     ///////// TRANSFORMATIONS /////////
    ///////////////////////////////////

    /** Returns a transformation which encodes the data in Base64 using the basic encoder and decoder. */
    public static Transformation base64() { return base64(Base64.getEncoder(), Base64.getDecoder()); }

    /** Returns a transformation which encodes the data in Base64 using the given encoder and decoder. */
    public static Transformation base64(Base64.Encoder e, Base64.Decoder d) {
        return new Base64Transformation(requireNonNull(e), requireNonNull(d));
    }

    /** Returns a transformation which buffers I/O using a buffer size of {@value Store#BUFSIZE} bytes. */
    public static Transformation buffer() { return buffer(Store.BUFSIZE); }

    /** Returns a transformation which buffers I/O using the given buffer size in bytes. */
    public static Transformation buffer(int size) { return new BufferedIOTransformation(size); }

    /**
     * Returns a transformation which encrypts or decrypts the data using the given cipher suppliers for output and
     * input.
     *
     * @param ciphers a function which returns an initialized cipher.
     *                If its input parameter is {@code false}, then the returned cipher must be initialized for input,
     *                otherwise it must be initialized for output.
     */
    public static Transformation cipher(XFunction<? super Boolean, ? extends Cipher> ciphers) {
        requireNonNull(ciphers);
        return new CipherTransformation(() -> ciphers.apply(false), () -> ciphers.apply(true));
    }

    /**
     * Returns a transformation which compresses the data using a ZIP deflater with the default compression level.
     */
    public static Transformation deflate() { return deflate(Deflater.DEFAULT_COMPRESSION); }

    /**
     * Returns a transformation which compresses the data using a ZIP deflater with the given compression level.
     *
     * @see Deflater
     */
    public static Transformation deflate(int level) {
        if (level < Deflater.DEFAULT_COMPRESSION || Deflater.BEST_COMPRESSION < level) {
            throw new IllegalArgumentException(level + " is not in the range from " + Deflater.DEFAULT_COMPRESSION + " to " + Deflater.BEST_COMPRESSION + ".");
        }
        return deflate(() -> new Deflater(level), Inflater::new);
    }

    /** Returns a transformation which compresses the data using a ZIP deflater. */
    public static Transformation deflate(XSupplier<Deflater> deflaterSupplier, XSupplier<Inflater> inflaterSupplier) {
        return new DeflateTransformation(requireNonNull(deflaterSupplier), requireNonNull(inflaterSupplier));
    }

    /** Returns a transformation which produces the GZIP compression format. */
    public static Transformation gzip() { return new GZIPTransformation(); }

    /** Returns the identity transformation. */
    public static Transformation identity() { return Transformation.IDENTITY; }

    /**
     * Returns a transformation which decompresses the data using a ZIP inflater.
     * For the reverse operation, the transformation uses a ZIP deflater with the default compression level.
     */
    public static Transformation inflate() { return inflate(Deflater.DEFAULT_COMPRESSION); }

    /**
     * Returns a transformation which decompresses the data using a ZIP inflater.
     * For the reverse operation, the transformation uses a ZIP deflater with the given compression level.
     */
    public static Transformation inflate(int level) {
        if (level < Deflater.DEFAULT_COMPRESSION || Deflater.BEST_COMPRESSION < level) {
            throw new IllegalArgumentException(level + " is not in the range from " + Deflater.DEFAULT_COMPRESSION + " to " + Deflater.BEST_COMPRESSION + ".");
        }
        return inflate(Inflater::new, () -> new Deflater(level));
    }

    /** Returns a transformation which decompresses the data using a ZIP inflater. */
    public static Transformation inflate(XSupplier<Inflater> inflaterSupplier, XSupplier<Deflater> deflaterSupplier) {
        return new InflateTransformation(requireNonNull(inflaterSupplier), requireNonNull(deflaterSupplier));
    }

    /**
     * Returns a transformation which inverses the given transformation by buffering the entire data on the heap.
     * <p>
     * This is a general purpose implementation which incurs buffering the entire data produced by the original
     * transformation, so use with care!
     * For any given transformation, it's advisable to provide a specialized implementation of the inverse
     * transformation which does not incur this overhead.
     */
    public static Transformation inverse(Transformation t) { return inverse(t, (XSupplier<Store>) BIOS::memory); }

    /**
     * Returns a transformation which inverses the given transformation by buffering the entire data in a temporary
     * store obtained from the given supplier.
     * <p>
     * This is a general purpose implementation which incurs buffering the entire data produced by the original
     * transformation, so use with care!
     * For any given transformation, it's advisable to provide a specialized implementation of the inverse
     * transformation which does not incur this overhead.
     */
    public static Transformation inverse(Transformation t, XSupplier<Store> storeSupplier) {
        requireNonNull(storeSupplier);
        return inverse(t, (Socket<Buffer>) () -> Buffer.of(storeSupplier.get()));
    }

    /**
     * Returns a transformation which inverses the given transformation by buffering the entire data in a loaned
     * buffer.
     * <p>
     * This is a general purpose implementation which incurs buffering the entire data produced by the original
     * transformation, so use with care!
     * For any given transformation, it's advisable to provide a specialized implementation of the inverse
     * transformation which does not incur this overhead.
     */
    public static Transformation inverse(Transformation t, Socket<Buffer> bufferSocket) {
        return new BufferedInverseTransformation(requireNonNull(t), requireNonNull(bufferSocket));
    }

    /** Returns a transformation which rotates each ASCII letter by 13 positions. */
    public static Transformation rot13() { return rot(13); }

    /** Returns a transformation which rotates each ASCII letter by the given number of positions. */
    public static Transformation rot(int positions) { return new ROTTransformation(positions); }

      ///////////////////////////
     ///////// SOURCES /////////
    ///////////////////////////

    /**
     * Returns a source which loads the resource with the given {@code name} using
     * {@link ClassLoader#getSystemResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     */
    public static Source resource(String name) {
        return () -> () -> Optional
                .ofNullable(ClassLoader.getSystemResourceAsStream(name))
                .orElseThrow(() -> new FileNotFoundException(name));
    }

    /**
     * Returns a source which loads the resource with the given {@code name} using
     * {@link ClassLoader#getResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     * @param  classLoader
     *         The class loader to use for loading the resource.
     */
    public static Source resource(String name, ClassLoader classLoader) {
        return () -> () -> Optional
                .ofNullable(classLoader.getResourceAsStream(name))
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

    /** Returns a store for the given file. */
    public static Store file(File f) { return file(f, false); }

    /** Returns a store for the given file, potentially for appending to it if {@code append} is {@code true}. */
    public static Store file(final File f, final boolean append) {
        final PathStore store = path(f.toPath());
        return append ? store.onOutput(APPEND, CREATE) : store;
    }

    /** Returns a new in-memory store with the default buffer size. */
    public static Store memory() { return memory(BUFSIZE); }

    /** Returns a new in-memory store with the given buffer size. */
    public static Store memory(int bufferSize) { return new MemoryStore(bufferSize); }

    /** Returns a store for the given path. */
    public static PathStore path(Path p) { return new RealPathStore(requireNonNull(p)); }

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
    public interface PathStore extends Store {

        /** Returns a new path store which uses the given open options on input. */
        PathStore onInput(OpenOption... options);

        /** Returns a new path store which uses the given open options on output. */
        PathStore onOutput(OpenOption... options);
    }

      //////////////////////////////////
     ///////// ARCHIVE STORES /////////
    //////////////////////////////////

    /** Returns an archive store for transparent access to the given directory. */
    public static ArchiveStore<Path> directory(File directory) { return directory(directory.toPath()); }

    /** Returns an archive store for transparent access to the given directory. */
    public static ArchiveStore<Path> directory(Path directory) { return new DirectoryStore(requireNonNull(directory)); }

    /** Returns an archive store for access to the given JAR file. */
    public static ArchiveStore<ZipEntry> jar(final File file) {
        requireNonNull(file);
        return new ArchiveStore<ZipEntry>() {

            @Override
            public Socket<ArchiveInput<ZipEntry>> input() { return () -> new ZipFileAdapter(new ZipFile(file)); }

            @Override
            public Socket<ArchiveOutput<ZipEntry>> output() {
                return () -> new JarOutputStreamAdapter(new JarOutputStream(new FileOutputStream(file)));
            }
        };
    }

    /** Returns an archive store for access to the given ZIP file. */
    public static ArchiveStore<ZipEntry> zip(final File file) {
        requireNonNull(file);
        return new ArchiveStore<ZipEntry>() {

            @Override
            public Socket<ArchiveInput<ZipEntry>> input() { return () -> new ZipFileAdapter(new ZipFile(file)); }

            @Override
            public Socket<ArchiveOutput<ZipEntry>> output() {
                return () -> new ZipOutputStreamAdapter(new ZipOutputStream(new FileOutputStream(file)));
            }
        };
    }

      /////////////////////////////
     ///////// UTILITIES /////////
    /////////////////////////////

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
    public static void copy(Source source, Sink sink) throws Exception { copy(source.input(), sink.output()); }

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
        input.accept(in -> output.accept(out -> Copy.cat(in, out)));
    }

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
    public static void copy(final ArchiveSource<?> source, final ArchiveSink<?> sink) throws Exception {
        source.acceptReader(input -> sink.acceptWriter(output -> {
            for (ArchiveEntrySource<?> entry : input) {
                entry.copyTo(output.sink(entry.name()));
            }
        }));
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
}
