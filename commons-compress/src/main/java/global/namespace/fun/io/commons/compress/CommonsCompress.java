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
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
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
 * This facade provides static factory methods for filters and archive file stores.
 * It depends on Apache Commons Compress and provides best accuracy and performance for accessing JAR and ZIP files.
 *
 * @author Christian Schlichtherle
 */
public final class CommonsCompress {

    private CommonsCompress() { }

      ///////////////////////////
     ///////// FILTERS /////////
    ///////////////////////////

    /** Returns a filter which compresses/decompresses data using the LZ4 block format with default parameters. */
    public static Filter blockLZ4() {
        return blockLZ4(BlockLZ4CompressorOutputStream.createParameterBuilder().build());
    }

    /** Returns a filter which compresses/decompresses data using the LZ4 block format with the given parameters. */
    public static Filter blockLZ4(Parameters p) { return new BlockLZ4Filter(requireNonNull(p)); }

    /** Returns a filter which compresses/decompresses data using the BZIP2 format with the maximum block size. */
    public static Filter bzip2() { return bzip2(MAX_BLOCKSIZE); }

    /** Returns a filter which compresses/decompresses data using the BZIP2 format with the given block size. */
    public static Filter bzip2(int blockSize) { return new BZIP2Filter(MAX_BLOCKSIZE); }

    /** Returns a filter which compresses/decompresses data using a ZIP deflater/inflater with default parameters. */
    public static Filter deflate() { return deflate(new DeflateParameters()); }

    /** Returns a filter which compresses/decompresses data using a ZIP deflater/inflater with the given parameters. */
    public static Filter deflate(DeflateParameters p) { return new DeflateFilter(requireNonNull(p)); }

    /** Returns a filter which compresses/decompresses data using the LZ4 frame format with default parameters. */
    public static Filter framedLZ4() { return framedLZ4(FramedLZ4CompressorOutputStream.Parameters.DEFAULT); }

    /** Returns a filter which compresses/decompresses data using the LZ4 frame format with the given parameters. */
    public static Filter framedLZ4(FramedLZ4CompressorOutputStream.Parameters p) {
        return new FramedLZ4Filter(requireNonNull(p));
    }

    /** Returns a filter which compresses/decompresses data using the Snappy frame format with default parameters. */
    public static Filter framedSnappy() {
        return framedSnappy(
                SnappyCompressorOutputStream.createParameterBuilder(SnappyCompressorInputStream.DEFAULT_BLOCK_SIZE).build(),
                FramedSnappyDialect.STANDARD);
    }

    /** Returns a filter which compresses/decompresses data using the Snappy frame format with the given parameters. */
    public static Filter framedSnappy(Parameters outputParameters, FramedSnappyDialect inputParameters) {
        return new FramedSnappyFilter(requireNonNull(outputParameters), requireNonNull(inputParameters));
    }

    /** Returns a filter which compresses/decompresses data using the GZIP format with default parameters. */
    public static Filter gzip() { return gzip(new GzipParameters()); }

    /** Returns a filter which compresses/decompresses data using the GZIP format with the given parameters. */
    public static Filter gzip(GzipParameters p) { return new GZIPFilter(requireNonNull(p)); }

    /**
     * Returns a filter which compresses/decompresses data using the LZMA format.
     *
     * @deprecated Use {@code global.namespace.fun.io.xz.XZ.lzma()} instead.
     */
    @Deprecated
    public static Filter lzma() { return new LZMAFilter(); }

    /**
     * Returns a filter which compresses/decompresses data using the LZMA2 format with the default preset.
     *
     * @deprecated Use {@code global.namespace.fun.io.xz.XZ.lzma2()} instead.
     */
    @Deprecated
    public static Filter lzma2() { return lzma2(LZMA2Options.PRESET_DEFAULT); }

    /**
     * Returns a filter which compresses/decompresses data using the LZMA2 format with the given preset.
     *
     * @deprecated Use {@code global.namespace.fun.io.xz.XZ.lzma2(preset)} instead.
     */
    @Deprecated
    public static Filter lzma2(int preset) { return new LZMA2Filter(preset); }

      //////////////////////////////////
     ///////// ARCHIVE STORES /////////
    //////////////////////////////////

    /** Returns an archive store for read/write access to the JAR file referenced by the given path. */
    public static ArchiveStore<ZipArchiveEntry> jar(final File path) {
        requireNonNull(path);
        return new ArchiveStore<ZipArchiveEntry>() {

            @Override
            public Socket<ArchiveInput<ZipArchiveEntry>> input() { return () -> new ZipFileAdapter(new ZipFile(path)); }

            @Override
            public Socket<ArchiveOutput<ZipArchiveEntry>> output() {
                return () -> new JarArchiveOutputStreamAdapter(new JarArchiveOutputStream(new FileOutputStream(path)));
            }
        };
    }

    /** Returns an archive store for read/write access to the JAR file referenced by the given path. */
    public static ArchiveStore<ZipArchiveEntry> jar(String path) { return jar(new File(path)); }

    /**
     * Returns an archive store for copy-only access to the 7zip file referenced by the given path.
     * The resulting archive store has very limited capabilities due to the constraints of the 7zip file format.
     * For example, you can't randomly access entries in a 7zip file because there is no central directory.
     * <p>
     * In fact, the only supported use case is to use the resulting archive store as a source or a sink for archive
     * copying.
     * This is still very powerful, because it allows you to pack or unpack a 7zip file from or to a directory
     * or to transform it from or to another archive file format, e.g. ZIP.
     */
    public static ArchiveStore<SevenZArchiveEntry> sevenz(final File path) {
        requireNonNull(path);
        return new ArchiveStore<SevenZArchiveEntry>() {

            @Override
            public Socket<ArchiveInput<SevenZArchiveEntry>> input() {
                return () -> new SevenZFileAdapter(new SevenZFile(path));
            }

            @Override
            public Socket<ArchiveOutput<SevenZArchiveEntry>> output() {
                return () -> new SevenZOutputFileAdapter(new SevenZOutputFile(path));
            }
        };
    }

    /**
     * Returns an archive store for copy-only access to the TAR file referenced by the given store.
     * The resulting archive store has very limited capabilities due to the constraints of the TAR file format.
     * For example, you can't just use an output stream socket to write a TAR entry because the size of a TAR entry must
     * be known in advance.
     * Similarly, you can't randomly access entries in a TAR file because there is no central directory.
     * <p>
     * In fact, the only supported use case is to use the resulting archive store as a source or a sink for archive
     * copying.
     * This is still very powerful, because it allows you to pack or unpack a TAR file from or to a directory
     * or to transform it from or to another archive file format, e.g. ZIP.
     * <p>
     * Note that the parameter is a {@link Store}, so you can apply any {@link Filter} to it, e.g.
     * {@link #gzip()}.
     */
    public static ArchiveStore<TarArchiveEntry> tar(final Store store) {
        requireNonNull(store);
        return new ArchiveStore<TarArchiveEntry>() {

            @Override
            public Socket<ArchiveInput<TarArchiveEntry>> input() {
                return store.input().map(in -> new TarArchiveInputStreamAdapter(new TarArchiveInputStream(in)));
            }

            @Override
            public Socket<ArchiveOutput<TarArchiveEntry>> output() {
                return store.output().map(out -> new TarArchiveOutputStreamAdapter(new TarArchiveOutputStream(out)));
            }
        };
    }

    /** Returns an archive store for read/write access to the ZIP file referenced by the given path. */
    public static ArchiveStore<ZipArchiveEntry> zip(final File path) {
        requireNonNull(path);
        return new ArchiveStore<ZipArchiveEntry>() {

            @Override
            public Socket<ArchiveInput<ZipArchiveEntry>> input() { return () -> new ZipFileAdapter(new ZipFile(path)); }

            @Override
            public Socket<ArchiveOutput<ZipArchiveEntry>> output() {
                return () -> new ZipArchiveOutputStreamAdapter(new ZipArchiveOutputStream(path));
            }
        };
    }

    /** Returns an archive store for read/write access to the ZIP file referenced by the given path. */
    public static ArchiveStore<ZipArchiveEntry> zip(String path) { return zip(new File(path)); }
}
