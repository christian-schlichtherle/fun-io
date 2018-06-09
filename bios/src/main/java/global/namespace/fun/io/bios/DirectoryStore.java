/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;

import static global.namespace.fun.io.api.ArchiveEntryNames.requireInternal;
import static global.namespace.fun.io.bios.BIOS.copy;

/**
 * Provides access to directories as if they were archive files.
 * This is handy for testing purposes or if you want to diff/patch two directories.
 *
 * @author Christian Schlichtherle
 */
final class DirectoryStore implements ArchiveStore<Path> {

    private final Path directory;
    private final String separator;

    DirectoryStore(final Path directory) {
        this.directory = directory;
        final FileSystem fs = directory.getFileSystem();
        this.separator = fs.getSeparator();
    }

    @Override
    public Socket<ArchiveInput<Path>> input() {
        return () -> new ArchiveInput<Path>() {

            @Override
            public Iterator<ArchiveEntrySource<Path>> iterator() {
                try {
                    return Files
                            .walk(directory)
                            .filter(p -> !p.equals(directory))
                            .map(this::source)
                            .iterator();
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public Optional<ArchiveEntrySource<Path>> source(final String name) {
                final Path path = resolve(name);
                return Optional.ofNullable(Files.exists(path) ? source(path) : null);
            }

            ArchiveEntrySource<Path> source(Path path) {
                return new ArchiveEntrySource<Path>() {

                    final String name = relativize(path);

                    @Override
                    public String name() { return isDirectory() ? name + '/' : name; }

                    @Override
                    public long size() {
                        try {
                            return Files.size(entry());
                        } catch (IOException ignored) {
                            return 0;
                        }
                    }

                    @Override
                    public boolean isDirectory() { return Files.isDirectory(path); }

                    @Override
                    public Path entry() { return path; }

                    @Override
                    public Socket<InputStream> input() {
                        return () -> {
                            if (isDirectory()) {
                                return new ByteArrayInputStream(new byte[0]);
                            } else {
                                return Files.newInputStream(entry());
                            }
                        };
                    }
                };
            }

            @Override
            public void close() { }
        };
    }

    @Override
    public Socket<ArchiveOutput<Path>> output() {
        return () -> new ArchiveOutput<Path>() {

            @Override
            public boolean isJar() { return false; }

            @Override
            public ArchiveEntrySink<Path> sink(String name) { return sink(resolve(name)); }

            ArchiveEntrySink<Path> sink(Path path) {
                return new ArchiveEntrySink<Path>() {

                    final String name = relativize(path);

                    @Override
                    public String name() { return isDirectory() ? name + '/' : name; }

                    @Override
                    public long size() {
                        try {
                            return Files.size(entry());
                        } catch (IOException ignored) {
                            return 0;
                        }
                    }

                    @Override
                    public boolean isDirectory() { return Files.isDirectory(path); }

                    @Override
                    public Path entry() { return path; }

                    @Override
                    public Socket<OutputStream> output() {
                        return () -> {
                            final Path entry = entry();
                            final Path parent = entry.getParent();
                            if (null != parent) {
                                Files.createDirectories(parent);
                            }
                            return Files.newOutputStream(entry);
                        };
                    }

                    @Override
                    public void copyFrom(final ArchiveEntrySource<?> source) throws Exception {
                        if (source.isDirectory()) {
                            Files.createDirectories(entry());
                        } else {
                            copy(source, this);
                        }
                    }
                };
            }

            @Override
            public void close()  { }
        };
    }

    private Path resolve(String name) {
        return directory.resolve(requireInternal(name).replace("/", separator));
    }

    private String relativize(Path path) {
        return directory.relativize(path).toString().replace(separator, "/");
    }
}
