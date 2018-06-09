/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.api;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Provides utility methods for archive entry names.
 *
 * @author Christian Schlichtherle
 */
public final class ArchiveEntryNames {

    private static final FileSystem fileSystem = FileSystems.getDefault();
    private static final String separator = fileSystem.getSeparator();
    private static final Path empty = fileSystem.getPath(""), dotDot = fileSystem.getPath("..");

    private ArchiveEntryNames() { }

    /**
     * Returns the normalized form of the given archive entry name.
     * The separator for the elements of the given name is {@code /}.
     *
     * @param name the archive entry name, where elements are separated by {@code /} and directory names end with a
     *             {@code /}.
     * @return the normalized form of the given archive entry name.
     * @throws IllegalArgumentException if the normalized form of the given archive entry name is absolute or empty or
     *                                  has {@code ..} as its first path segment.
     * @see #isInternal(String)
     */
    public static String requireInternal(final String name) {
        final Path normalized = normalize(name);
        if (isExternal(normalized)) {
            throw new IllegalArgumentException("A normalized archive entry name must not be absolute or empty or have `..` as its first path segment, but was `" + normalized + "`.");
        }
        String result = normalized.toString().replace(separator, "/");
        if (name.endsWith("/")) {
            result += "/";
        }
        return result.equals(name) ? name : result;
    }

    private static Path normalize(String name) {
        return fileSystem.getPath(name.replace("/", separator)).normalize();
    }

    /**
     * Returns {@code true} if and only if the normalized form of the given archive entry name is not absolute and not
     * empty and does not have {@code ..} as its first path segment.
     *
     * @see #requireInternal(String)
     */
    public static boolean isInternal(String name) { return !isExternal(normalize(name)); }

    private static boolean isExternal(Path normalized) {
        return normalized.isAbsolute() || normalized.equals(empty) || normalized.getName(0).equals(dotDot);
    }
}
