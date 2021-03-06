/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveOutputStream;

import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;

/**
 * Adapts a {@link JarOutputStream} to an {@link ArchiveOutputStream}.
 *
 * @author Christian Schlichtherle
 */
final class JarOutputStreamAdapter extends ZipOutputStreamAdapter {

    JarOutputStreamAdapter(JarOutputStream jar) { super(jar); }

    /** Returns {@code true}. */
    @Override
    public boolean isJar() { return true; }

    @Override
    public ArchiveEntrySink sink(String name) { return sink(new JarEntry(requireInternal(name))); }
}
