/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;

import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;

/**
 * Adapts a {@link JarArchiveOutputStream} to an {@link ArchiveOutputStream}.
 *
 * @author Christian Schlichtherle
 */
final class JarArchiveOutputStreamAdapter extends ZipArchiveOutputStreamAdapter {

    JarArchiveOutputStreamAdapter(JarArchiveOutputStream jar) { super(jar); }

    /** Returns {@code true}. */
    @Override
    public boolean isJar() { return true; }

    @Override
    public ArchiveEntrySink sink(String name) {
        return sink(new JarArchiveEntry(requireInternal(name)));
    }
}
