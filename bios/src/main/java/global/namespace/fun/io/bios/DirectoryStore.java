/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;

import static global.namespace.fun.io.bios.BIOS.copy;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Provides access to directories as if they were archive files.
 * This is handy for testing purposes or if you want to diff/patch two directories.
 *
 * @author Christian Schlichtherle
 */
final class DirectoryStore implements ArchiveStore<Path> {

    private final Path directory;

    DirectoryStore(final Path directory) { this.directory = directory; }

    @Override
    public Socket<ArchiveInput<Path>> input() {
        return () -> new ArchiveInput<Path>() {

            @Override
            public Iterator<ArchiveEntrySource<Path>> iterator() {
                try {
                    return Files
                            .walk(directory)
                            .filter(p -> !p.equals(directory))
                            .map(path -> pathSource(directory.relativize(path).toString()))
                            .iterator();
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            public Optional<ArchiveEntrySource<Path>> source(final String name) {
                final Path path = resolve(name);
                return exists(path) ? of(pathSource(name)) : empty();
            }

            public void close() { }
        };
    }

    @Override
    public Socket<ArchiveOutput<Path>> output() {
        return () -> new ArchiveOutput<Path>() {

            public boolean isJar() { return false; }

            public ArchiveEntrySink<Path> sink(String name) { return pathSink(name); }

            public void close()  { }
        };
    }

    private ArchiveEntrySource<Path> pathSource(String relativePath) {
        return new ArchiveEntrySource<Path>() {

            @Override
            public String name() { return relativePath; }

            @Override
            public long size() {
                try {
                    return Files.size(entry());
                } catch (IOException ignored) {
                    return -1;
                }
            }

            @Override
            public boolean isDirectory() { return Files.isDirectory(resolvedPath()); }

            @Override
            public Path entry() { return get(relativePath); }

            @Override
            public Socket<InputStream> input() { return () -> newInputStream(resolvedPath()); }

            Path resolvedPath() { return resolve(relativePath); }
        };
    }

    private ArchiveEntrySink<Path> pathSink(String relativePath) {
        return new ArchiveEntrySink<Path>() {

            @Override
            public String name() { return relativePath; }

            @Override
            public long size() {
                try {
                    return Files.size(entry());
                } catch (IOException ignored) {
                    return -1;
                }
            }

            @Override
            public boolean isDirectory() { return Files.isDirectory(resolvedPath()); }

            @Override
            public Path entry() { return get(name()); }

            @Override
            public Socket<OutputStream> output() {
                return () -> {
                    final Path path = resolvedPath();
                    final Path parent = path.getParent();
                    if (null != parent) {
                        createDirectories(parent);
                    }
                    return newOutputStream(path);
                };
            }

            @Override
            public void copyFrom(ArchiveEntrySource<?> source) throws Exception { copy(source, this); }

            Path resolvedPath() { return resolve(relativePath); }
        };
    }

    private Path resolve(String relativePath) { return directory.resolve(relativePath); }
}
