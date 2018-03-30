/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.util;

import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.Immutable;

/**
 * Provides functions for dealing with resources on the class path.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public final class Resources {

    public static URL locate(final String name) throws ServiceConfigurationError {
        final Enumeration<URL> en;
        final URL url;
        try {
            en = Thread.currentThread()
                    .getContextClassLoader().getResources(name);
            url = en.nextElement();
        } catch (Exception ex) {
            throw new ServiceConfigurationError(String.format(Locale.ENGLISH,
                    "Could not locate the resource %s on the class path.", name), ex);
        }
        if (en.hasMoreElements())
            Logger  .getLogger(Resources.class.getName())
                    .log(Level.WARNING,
                        "There is more than one resource with the name {0} on the class path.\nSelecting {1} .",
                        new Object[] { name, url });
        return url;
    }

    private Resources() { }
}
