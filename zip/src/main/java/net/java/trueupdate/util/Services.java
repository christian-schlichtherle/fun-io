/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.util;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import javax.annotation.concurrent.Immutable;

/**
 * Loads a service implementation using the current thread's context class
 * loader.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public final class Services {

    /**
     * Returns an instance of the given service interface or class.
     * The service implementation is loaded using the current thread's context
     * class loader.
     *
     * @throws ServiceConfigurationError if no implementation of the given
     *         service interface or class can be found on the class path.
     * @see    ServiceLoader#load(Class)
     */
    public static <T> T load(final Class<T> service) {
        try {
            return ServiceLoader.load(service).iterator().next();
        } catch (NoSuchElementException ex) {
            throw new ServiceConfigurationError(String.format(Locale.ENGLISH,
                    "Could not find an implementation of the service %s on the class path.",
                    service), ex);
        }
    }

    private Services() { }
}
