/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.archive.io.bios;

import global.namespace.archive.io.api.ArchiveFileInput;
import global.namespace.archive.io.api.ArchiveFileOutput;
import global.namespace.archive.io.api.ArchiveFileStore;
import global.namespace.fun.io.api.Socket;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.requireNonNull;

/**
 * Provides access to directories, JAR and ZIP files.
 * This package depends on Java Runtime Environment (JRE) only.
 * The abbreviation stands for Basic Input/Output System (pun intended).
 *
 * @author Christian Schlichtherle
 */
public class BIOS {

    private BIOS() { }

    /** Returns an archive file store for the given directory. */
    public static ArchiveFileStore<Path> directory(File directory) { return directory(directory.toPath()); }

    /** Returns an archive file store for the given directory. */
    public static ArchiveFileStore<Path> directory(Path directory) {
        return new DirectoryStore(requireNonNull(directory));
    }

    /** Returns an archive file store for the given JAR file. */
    public static ArchiveFileStore<ZipEntry> jar(final File file) {
        requireNonNull(file);
        return new ArchiveFileStore<ZipEntry>() {

            @Override
            public Socket<ArchiveFileInput<ZipEntry>> input() { return () -> new ZipFileAdapter(new ZipFile(file)); }

            @Override
            public Socket<ArchiveFileOutput<ZipEntry>> output() {
                return () -> new JarOutputStreamAdapter(new JarOutputStream(new FileOutputStream(file)));
            }
        };
    }

    /** Returns an archive file store for the given ZIP file. */
    public static ArchiveFileStore<ZipEntry> zip(final File file) {
        requireNonNull(file);
        return new ArchiveFileStore<ZipEntry>() {

            @Override
            public Socket<ArchiveFileInput<ZipEntry>> input() { return () -> new ZipFileAdapter(new ZipFile(file)); }

            @Override
            public Socket<ArchiveFileOutput<ZipEntry>> output() {
                return () -> new ZipOutputStreamAdapter(new ZipOutputStream(new FileOutputStream(file)));
            }
        };
    }
}
