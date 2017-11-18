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
import java.util.prefs.Preferences;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static global.namespace.fun.io.api.Store.BUFSIZE;
import static java.util.Objects.requireNonNull;

/**
 * Provides static factory methods for loans, stores, transformations and codecs.
 * The abbreviation means Basic I/O System.
 *
 * @author Christian Schlichtherle
 */
public final class BIOS {

    private BIOS() { }

    ///////////////////////////
    ////////// LOANS //////////
    ///////////////////////////

    /**
     * Returns a loan for the given output stream which will never close it.
     * This is intended to be used for data streaming or for interoperability with other libraries and frameworks where
     * you dont want the given output stream to get closed by the returned loan.
     * Upon a call to the {@code close()} method on the loaned output stream, the {@code flush()} method gets called on
     * the given output stream.
     */
    public static Loan<OutputStream> stream(OutputStream os) {
        requireNonNull(os);
        return () -> new FilterOutputStream(os) {

            @Override
            public void close() throws IOException { out.flush(); }
        };
    }

    /**
     * Returns a loan for the given input stream which will never close it.
     * This is intended to be used for data streaming or for interoperability with other libraries and frameworks where
     * you dont want the given input stream to get closed by the returned loan.
     */
    public static Loan<InputStream> stream(InputStream is) {
        requireNonNull(is);
        return () -> new FilterInputStream(is) {

            @Override
            public void close() throws IOException { }
        };
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
    public static Transformation deflate(XSupplier<Deflater> ds, XSupplier<Inflater> is) {
        return new DeflateTransformation(requireNonNull(ds), requireNonNull(is));
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
    public static Transformation inflate(XSupplier<Inflater> is, XSupplier<Deflater> ds) {
        return new InflateTransformation(requireNonNull(is), requireNonNull(ds));
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
    public static Transformation inverse(Transformation t, XSupplier<Store> ss) {
        requireNonNull(ss);
        return inverse(t, (Loan<Buffer>) () -> Buffer.of(ss.get()));
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
    public static Transformation inverse(Transformation t, Loan<Buffer> bl) {
        return new BufferedInverseTransformation(requireNonNull(t), requireNonNull(bl));
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
    public static Codec xmlCodec(XFunction<? super OutputStream, ? extends XMLEncoder> e,
                                 XFunction<? super InputStream, ? extends XMLDecoder> d) {
        return new XMLCodec(requireNonNull(e), requireNonNull(d));
    }
}
