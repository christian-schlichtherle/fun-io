/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta.dto;

import java.io.Serializable;

/** @author Christian Schlichtherle */
public final class DeltaDTO implements Serializable {

    public String algorithm;

    public int numBytes;

    public EntryNameAndTwoDigestValuesDTO[] changed;

    public EntryNameAndDigestValueDTO[] unchanged, added, removed;
}
