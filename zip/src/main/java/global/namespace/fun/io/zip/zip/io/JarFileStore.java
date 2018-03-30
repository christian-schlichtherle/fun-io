/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * A file based JAR store.
 *
 * @author Christian Schlichtherle
 */
public class JarFileStore extends ZipFileStore {

    public JarFileStore(File file) { super(file); }

    @Override public ZipInput input() throws IOException {
        return new ZipFileAdapter(new JarFile(file));
    }

    @Override public ZipOutput output() throws IOException {
        return new JarOutputStreamAdapter(new JarOutputStream(
                new FileOutputStream(file)));
    }
}
