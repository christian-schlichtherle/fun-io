/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import java.io.IOException;

/**
 * Indicates that an archive entry is missing.
 *
 * @author Christian Schlichtherle
 */
public class MissingArchiveEntryException extends IOException {

    private static final long serialVersionUID = 0L;

    MissingArchiveEntryException(String message) { super(message); }
}
