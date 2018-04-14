/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.archive.io.commons.compress;

import global.namespace.archive.io.api.ArchiveEntrySink;
import global.namespace.archive.io.api.ArchiveFileOutput;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

/**
 * Adapts a {@link JarArchiveOutputStream} to an {@link ArchiveFileOutput}.
 *
 * @author Christian Schlichtherle
 */
final class JarArchiveOutputStreamAdapter extends ZipArchiveOutputStreamAdapter {

    JarArchiveOutputStreamAdapter(JarArchiveOutputStream jar) { super(jar); }

    /** Returns {@code true}. */
    @Override
    public boolean isJar() { return true; }

    @Override
    public ArchiveEntrySink<ZipArchiveEntry> sink(String name) { return sink(new JarArchiveEntry(name)); }
}
