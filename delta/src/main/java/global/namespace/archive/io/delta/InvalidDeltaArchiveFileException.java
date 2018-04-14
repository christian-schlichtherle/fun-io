/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.archive.io.delta;

import java.io.IOException;

/**
 * Indicates that the integrity of the delta archive has been violated.
 *
 * @author Christian Schlichtherle
 */
public class InvalidDeltaArchiveFileException extends IOException {

    private static final long serialVersionUID = 0L;

    InvalidDeltaArchiveFileException(Throwable cause) { super(cause); }
}
