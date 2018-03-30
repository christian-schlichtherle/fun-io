/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.io;

import java.util.zip.ZipOutputStream;
import javax.annotation.WillNotClose;
import net.java.trueupdate.core.io.Task;

/**
 * Executes a task on a {@link ZipOutputStream}.
 *
 * @see ZipSinks#execute
 * @author Christian Schlichtherle
 */
public interface ZipOutputTask<V, X extends Exception>
extends Task<V, ZipOutput, X> {

    @Override V execute(@WillNotClose ZipOutput resource) throws X;
}
