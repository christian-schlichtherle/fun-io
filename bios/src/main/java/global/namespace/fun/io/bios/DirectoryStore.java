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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;

import static global.namespace.fun.io.bios.BIOS.copy;

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
                            .map(path -> {
                                String name = directory.relativize(path).toString();
                                if (Files.isDirectory(path)) {
                                    name += '/';
                                }
                                return pathSource(name);
                            })
                            .iterator();
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public Optional<ArchiveEntrySource<Path>> source(String name) {
                return Optional.ofNullable(Files.exists(resolve(name)) ? pathSource(name) : null);
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
            public ArchiveEntrySink<Path> sink(String name) { return pathSink(name); }

            @Override
            public void close()  { }
        };
    }

    private ArchiveEntrySource<Path> pathSource(String name) {
        return new ArchiveEntrySource<Path>() {

            @Override
            public String name() { return name; }

            @Override
            public long size() {
                try {
                    return Files.size(path());
                } catch (IOException ignored) {
                    return 0;
                }
            }

            @Override
            public boolean isDirectory() { return name().endsWith("/"); }

            @Override
            public Path entry() { return Paths.get(name()); }

            @Override
            public Socket<InputStream> input() {
                return () -> {
                    if (isDirectory()) {
                        return new ByteArrayInputStream(new byte[0]);
                    } else {
                        return Files.newInputStream(path());
                    }
                };
            }

            Path path() { return resolve(name()); }
        };
    }

    private ArchiveEntrySink<Path> pathSink(String name) {
        return new ArchiveEntrySink<Path>() {

            @Override
            public String name() { return name; }

            @Override
            public long size() {
                try {
                    return Files.size(path());
                } catch (IOException ignored) {
                    return 0;
                }
            }

            @Override
            public boolean isDirectory() { return name().endsWith("/"); }

            @Override
            public Path entry() { return Paths.get(name()); }

            @Override
            public Socket<OutputStream> output() {
                return () -> {
                    final Path path = path();
                    final Path parent = path.getParent();
                    if (null != parent) {
                        Files.createDirectories(parent);
                    }
                    return Files.newOutputStream(path);
                };
            }

            @Override
            public void copyFrom(final ArchiveEntrySource<?> source) throws Exception {
                if (source.isDirectory()) {
                    Files.createDirectories(path());
                } else {
                    copy(source, this);
                }
            }

            Path path() { return resolve(name()); }
        };
    }

    private Path resolve(String name) { return directory.resolve(name); }
}
