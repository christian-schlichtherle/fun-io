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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static global.namespace.fun.io.api.Store.BUFSIZE;
import static java.util.Objects.requireNonNull;

/**
 * Provides static factory methods for sockets, stores, transformations, codecs and more.
 * The abbreviation stands for Basic Input/Output System (pun intended).
 *
 * @author Christian Schlichtherle
 */
public final class BIOS {

    private BIOS() { }

    /////////////////////////////
    ////////// SOCKETS //////////
    /////////////////////////////

    /**
     * Returns an input stream socket which loads the resource with the given {@code name} using
     * {@link ClassLoader#getSystemResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     */
    public static Socket<InputStream> resource(String name) {
        return () -> Optional
                .ofNullable(ClassLoader.getSystemResourceAsStream(name))
                .orElseThrow(() -> new FileNotFoundException(name));
    }

    /**
     * Returns an input stream socket which loads the resource with the given {@code name} using
     * {@link ClassLoader#getResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     * @param  classLoader
     *         The class loader to use for loading the resource.
     */
    public static Socket<InputStream> resource(String name, ClassLoader classLoader) {
        return () -> Optional
                .ofNullable(classLoader.getResourceAsStream(name))
                .orElseThrow(() -> new FileNotFoundException(name));
    }

    /**
     * Returns an input stream socket which reads from standard input without ever closing it.
     *
     * @see #stream(InputStream)
     */
    public static Socket<InputStream> stdin() { return stream(System.in); }

    /**
     * Returns a socket which will never close the given input stream.
     * This is intended to be used for data streaming or for interoperability with other libraries and frameworks where
     * you dont want the given input stream to get closed by the returned socket.
     */
    public static Socket<InputStream> stream(InputStream in) {
        requireNonNull(in);
        return () -> new UncloseableInputStream(in);
    }

    /**
     * Returns an output stream socket which writes to standard output without ever closing it.
     *
     * @see #stream(OutputStream)
     */
    public static Socket<OutputStream> stdout() { return stream(System.out); }

    /**
     * Returns an output stream socket which writes to standard error without ever closing it.
     *
     * @see #stream(OutputStream)
     */
    public static Socket<OutputStream> stderr() { return stream(System.err); }

    /**
     * Returns a socket which will never close the given output stream.
     * This is intended to be used for data streaming or for interoperability with other libraries and frameworks where
     * you dont want the given output stream to get closed by the returned socket.
     * Upon a call to the {@code close()} method on the loaned output stream, the {@code flush()} method gets called on
     * the given output stream.
     */
    public static Socket<OutputStream> stream(OutputStream out) {
        requireNonNull(out);
        return () -> new UncloseableOutputStream(out);
    }

    ////////////////////////////
    ////////// STORES //////////
    ////////////////////////////

    /** Returns a new memory store with the default buffer size. */
    public static Store memoryStore() { return memoryStore(BUFSIZE); }

    /** Returns a new memory store with the given buffer size. */
    public static Store memoryStore(int bufferSize) { return new MemoryStore(bufferSize); }

    /** Returns a path store for the given file. */
    public static Store pathStore(Path p) { return new PathStore(requireNonNull(p)); }

    /** Returns a store for the system preferences node for the package of the given class and the given key. */
    public static Store systemPreferencesStore(Class<?> classInPackage, String key) {
        return preferencesStore(Preferences.systemNodeForPackage(classInPackage), key);
    }

    /** Returns a store for the user preferences node for the package of the given class and the given key. */
    public static Store userPreferencesStore(Class<?> classInPackage, String key) {
        return preferencesStore(Preferences.userNodeForPackage(classInPackage), key);
    }

    /** Returns a preferences store for the given preferences node and key. */
    public static Store preferencesStore(Preferences p, String key) {
        return new PreferencesStore(requireNonNull(p), requireNonNull(key));
    }

    /////////////////////////////////////
    ////////// TRANSFORMATIONS //////////
    /////////////////////////////////////

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
     */
    public static Transformation cipher(XFunction<? super Boolean, ? extends Cipher> ciphers) {
        requireNonNull(ciphers);
        return new CipherTransformation(() -> ciphers.apply(true), () -> ciphers.apply(false));
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
    public static Transformation inverse(Transformation t) { return inverse(t, (XSupplier<Store>) BIOS::memoryStore); }

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

    ////////////////////////////
    ////////// CODECS //////////
    ////////////////////////////

    /**
     * Uses {@link Marshaller}s and {@link Unmarshaller}s derived from the given {@link JAXBContext} to encode and
     * decode object graphs to and from octet streams.
     */
    public static Codec jaxbCodec(JAXBContext c) { return new JAXBCodec(requireNonNull(c)); }

    /**
     * Uses {@link ObjectOutputStream}s and {@link ObjectInputStream}s to encode and decode object graphs to and from
     * octet streams.
     */
    public static Codec serializationCodec() { return new SerializationCodec(); }

    /**
     * Uses new {@link XMLEncoder}s and {@link XMLDecoder}s to encode and decode object graphs to and from octet
     * streams.
     */
    public static Codec xmlCodec() { return xmlCodec(XMLEncoder::new, XMLDecoder::new); }

    /**
     * Uses new {@link XMLEncoder}s and {@link XMLDecoder}s obtained by the given functions in order to encode and
     * decode object graphs to and from octet streams.
     */
    public static Codec xmlCodec(XFunction<? super OutputStream, ? extends XMLEncoder> xmlEncoders,
                                 XFunction<? super InputStream, ? extends XMLDecoder> xmlDecoders) {
        return new XMLCodec(requireNonNull(xmlEncoders), requireNonNull(xmlDecoders));
    }

    ////////////////////////////
    //////// UTILITIES /////////
    ////////////////////////////

    /**
     * Copies the data from the given source to the given sink.
     * <p>
     * The implementation in this class is suitable for only small amounts of data, say a few kilobytes.
     */
    public static void copy(Source source, Sink sink) throws Exception { copy(source.input(), sink.output()); }

    /**
     * Copies the data from the given input to the given output.
     * <p>
     * The implementation in this class is suitable for only small amounts of data, say a few kilobytes.
     */
    public static void copy(final Socket<? extends InputStream> input, final Socket<? extends OutputStream> output)
    throws Exception {
        input.accept(in -> {
            output.accept(out -> {
                final byte[] buffer = new byte[Store.BUFSIZE];
                for (int read; 0 <= (read = in.read(buffer)); ) {
                    out.write(buffer, 0, read);
                }
            });
        });
    }

    /**
     * Returns a deep clone of the given object by serializing it to a memory store and de-serializing it again.
     * The memory store uses {@value Store#BUFSIZE} bytes as its initial buffer size.
     *
     * @see #serializationCodec()
     * @see #memoryStore()
     */
    public static <T extends Serializable> T clone(T t) throws Exception { return clone(t, Store.BUFSIZE); }

    /**
     * Returns a deep clone of the given object by serializing it to a memory store and de-serializing it again.
     * The memory store uses the given number of byes as its initial buffer size.
     *
     * @see #serializationCodec()
     * @see #memoryStore(int)
     */
    public static <T extends Serializable> T clone(T t, int bufferSize) throws Exception {
        return serializationCodec().connect(memoryStore(bufferSize)).clone(t);
    }
}
