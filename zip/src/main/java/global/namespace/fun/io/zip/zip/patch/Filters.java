/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.patch;

/**
 * A filter for ZIP entry names.
 *
 * @author Christian Schlichtherle
 */
interface EntryNameFilter {

    /**
     * Returns {@code true} if and only if the filter accepts the given ZIP
     * entry name.
     */
    boolean accept(String name);
}

/**
 * A filter which accepts all ZIP entry names.
 *
 * @author Christian Schlichtherle
 */
final class AcceptAllEntryNameFilter implements EntryNameFilter {

    @Override public boolean accept(String name) { return true; }
}

/**
 * Inverts another filter.
 *
 * @author Christian Schlichtherle
 */
final class InverseEntryNameFilter implements EntryNameFilter {

    private final EntryNameFilter filter;

    InverseEntryNameFilter(final EntryNameFilter filter) {
        assert null != filter;
        this.filter = filter;
    }

    @Override public boolean accept(String name) {
        return !filter.accept(name);
    }
}

/**
 * Accepts only entry sources with the name "META-INF/MANIFEST.MF".
 *
 * @author Christian Schlichtherle
 */
final class ManifestEntryNameFilter implements EntryNameFilter {

    @Override public boolean accept(String name) {
        return "META-INF/MANIFEST.MF".equals(name);
    }
}

/**
 * Decorates another filter to suppress ZIP entries for directories.
 *
 * @author Christian Schlichtherle
 */
final class NoDirectoryEntryNameFilter implements EntryNameFilter {

    private final EntryNameFilter filter;

    NoDirectoryEntryNameFilter(final EntryNameFilter filter) {
        assert null != filter;
        this.filter = filter;
    }

    @Override public boolean accept(String name) {
        return !name.endsWith("/") && filter.accept(name);
    }
}
