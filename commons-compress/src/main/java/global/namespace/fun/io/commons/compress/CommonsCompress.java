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
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.*;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.compressors.snappy.FramedSnappyDialect;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import org.tukaani.xz.LZMA2Options;

import java.io.File;
import java.io.FileOutputStream;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream.MAX_BLOCKSIZE;

/**
 * This facade provides static factory methods for transformations and archive file stores.
 * It depends on Apache Commons Compress and provides best accuracy and performance for accessing JAR and ZIP files.
 *
 * @author Christian Schlichtherle
 */
public final class CommonsCompress {

    private CommonsCompress() { }

      ///////////////////////////////////
     ///////// TRANSFORMATIONS /////////
    ///////////////////////////////////

    /** Returns a transformation which produces the LZ4 block format using the default parameters. */
    public static Transformation blockLZ4() {
        return blockLZ4(BlockLZ4CompressorOutputStream.createParameterBuilder().build());
    }

    /** Returns a transformation which produces the LZ4 block format using the given parameters. */
    public static Transformation blockLZ4(Parameters p) { return new BlockLZ4Transformation(requireNonNull(p)); }

    /** Returns a transformation which produces the BZIP2 compression format using the maximum block size. */
    public static Transformation bzip2() { return bzip2(MAX_BLOCKSIZE); }

    /** Returns a transformation which produces the BZIP2 compression format using the given block size. */
    public static Transformation bzip2(int blockSize) { return new BZIP2Transformation(MAX_BLOCKSIZE); }

    /** Returns a transformation which compresses the data using a ZIP deflater with the default parameters. */
    public static Transformation deflate() { return deflate(new DeflateParameters()); }

    /** Returns a transformation which compresses the data using a ZIP deflater with the given parameters. */
    public static Transformation deflate(DeflateParameters p) { return new DeflateTransformation(requireNonNull(p)); }

    /** Returns a transformation which produces the LZ4 frame format using the default parameters. */
    public static Transformation framedLZ4() { return framedLZ4(FramedLZ4CompressorOutputStream.Parameters.DEFAULT); }

    /** Returns a transformation which produces the LZ4 frame format using the given parameters. */
    public static Transformation framedLZ4(FramedLZ4CompressorOutputStream.Parameters p) {
        return new FramedLZ4Transformation(requireNonNull(p));
    }

    /** Returns a transformation which produces the Snappy framing format using the default parameters. */
    public static Transformation framedSnappy() {
        return framedSnappy(
                SnappyCompressorOutputStream.createParameterBuilder(SnappyCompressorInputStream.DEFAULT_BLOCK_SIZE).build(),
                FramedSnappyDialect.STANDARD);
    }

    /** Returns a transformation which produces the Snappy framing format using the given parameters for output/input. */
    public static Transformation framedSnappy(Parameters outputParameters, FramedSnappyDialect inputParameters) {
        return new FramedSnappyTransformation(requireNonNull(outputParameters), requireNonNull(inputParameters));
    }

    /** Returns a transformation which produces the GZIP compression format using the default parameters. */
    public static Transformation gzip() { return gzip(new GzipParameters()); }

    /** Returns a transformation which produces the GZIP compression format using the given parameters. */
    public static Transformation gzip(GzipParameters p) { return new GZIPTransformation(requireNonNull(p)); }

    /** Returns a transformation which produces the LZMA compression format. */
    public static Transformation lzma() { return new LZMATransformation(); }

    /** Returns a transformation which produces the LZMA2 compression format using the default preset. */
    public static Transformation lzma2() { return lzma2(LZMA2Options.PRESET_DEFAULT); }

    /** Returns a transformation which produces the LZMA2 compression format using the given preset. */
    public static Transformation lzma2(int preset) { return new LZMA2Transformation(preset); }

      ////////////////////////////
     ///////// ARCHIVES /////////
    ////////////////////////////

    /** Returns an archive file store for access to the given JAR file. */
    public static ArchiveStore<ZipArchiveEntry> jar(final File file) {
        requireNonNull(file);
        return new ArchiveStore<ZipArchiveEntry>() {

            @Override
            public Socket<ArchiveInput<ZipArchiveEntry>> input() {
                return () -> new ZipFileAdapter(new ZipFile(file));
            }

            @Override
            public Socket<ArchiveOutput<ZipArchiveEntry>> output() {
                return () -> new JarArchiveOutputStreamAdapter(new JarArchiveOutputStream(new FileOutputStream(file)));
            }
        };
    }

    /** Returns an archive file store for access to the given ZIP file. */
    public static ArchiveStore<ZipArchiveEntry> zip(final File file) {
        requireNonNull(file);
        return new ArchiveStore<ZipArchiveEntry>() {

            @Override
            public Socket<ArchiveInput<ZipArchiveEntry>> input() {
                return () -> new ZipFileAdapter(new ZipFile(file));
            }

            @Override
            public Socket<ArchiveOutput<ZipArchiveEntry>> output() {
                return () -> new ZipArchiveOutputStreamAdapter(new ZipArchiveOutputStream(file));
            }
        };
    }
}
