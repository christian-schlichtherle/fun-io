/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.io;

import javax.annotation.WillCloseWhenClosed;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Adapts a {@link JarOutputStream} to a {@link ZipOutput}.
 *
 * @author Christian Schlichtherle
 */
public class JarOutputStreamAdapter extends ZipOutputStreamAdapter {

    public JarOutputStreamAdapter(@WillCloseWhenClosed JarOutputStream jar) {
        super(jar);
    }

    @Override public ZipEntry entry(String name) { return new JarEntry(name); }
}
