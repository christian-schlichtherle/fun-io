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

import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;
import static global.namespace.fun.io.spi.Copy.copy;

/**
 * Provides access to directories as if they were archive files.
 * This is handy for testing purposes or if you want to diff/patch two directories.
 *
 * @author Christian Schlichtherle
 */
final class DirectoryStore implements ArchiveStore {

    private final Path directory;
    private final String separator;

    DirectoryStore(final Path directory) {
        this.directory = directory;
        final FileSystem fs = directory.getFileSystem();
        this.separator = fs.getSeparator();
    }

    @Override
    public Socket<ArchiveInputStream> input() {
        return () -> new ArchiveInputStream() {

            @Override
            public Iterator<ArchiveEntrySource> iterator() {
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
            public Optional<ArchiveEntrySource> source(final String name) {
                final Path path = resolve(name);
                return Optional.ofNullable(Files.exists(path) ? source(path) : null);
            }

            ArchiveEntrySource source(Path path) {
                return new ArchiveEntrySource() {

                    final String name = relativize(path);

                    @Override
                    public Socket<InputStream> input() {
                        return () -> {
                            if (directory()) {
                                return new ByteArrayInputStream(new byte[0]);
                            } else {
                                return Files.newInputStream(path);
                            }
                        };
                    }

                    @Override
                    public String name() {
                        return directory() ? name + '/' : name;
                    }

                    @Override
                    public boolean directory() {
                        return Files.isDirectory(path);
                    }

                    @Override
                    public long size() {
                        try {
                            return Files.size(path);
                        } catch (IOException ignored) {
                            return 0;
                        }
                    }
                };
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Socket<ArchiveOutputStream> output() {
        return () -> new ArchiveOutputStream() {

            @Override
            public ArchiveEntrySink sink(String name) {
                return sink(resolve(name));
            }

            ArchiveEntrySink sink(Path path) {
                return new ArchiveEntrySink() {

                    @Override
                    public Socket<OutputStream> output() {
                        return () -> {
                            final Path parent = path.getParent();
                            if (null != parent) {
                                Files.createDirectories(parent);
                            }
                            return Files.newOutputStream(path);
                        };
                    }

                    @Override
                    public void copyFrom(final ArchiveEntrySource source) throws Exception {
                        if (source.directory()) {
                            Files.createDirectories(path);
                        } else {
                            copy(source, this);
                        }
                    }
                };
            }

            @Override
            public void close() {
            }
        };
    }

    private Path resolve(String name) {
        return directory.resolve(requireInternal(name).replace("/", separator));
    }

    private String relativize(Path path) {
        return directory.relativize(path).toString().replace(separator, "/");
    }
}
