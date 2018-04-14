/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.archive.io.delta.model;

import static java.util.Objects.requireNonNull;

/**
 * A Value Object which represents a archive entry name and two message digests in canonical string notation.
 *
 * @author Christian Schlichtherle
 */
public final class EntryNameAndTwoDigestValues {

    private final String name, baseDigestValue, updateDigestValue;

    /**
     * Default constructor.
     * The first and second message digest should not be equal.
     */
    public EntryNameAndTwoDigestValues(
            final String name,
            final String baseDigestValue,
            final String updateDigestValue) {
        this.name = requireNonNull(name);
        this.baseDigestValue = requireNonNull(baseDigestValue);
        this.updateDigestValue = requireNonNull(updateDigestValue);
        assert !baseDigestValue.equals(updateDigestValue);
    }

    /** Returns the archive entry name. */
    public String name() { return name; }

    /** Returns the message digest value of the archive entry in the base archive file. */
    public String baseDigestValue() { return baseDigestValue; }

    /** Returns the message digest value of the archive entry in the update archive file. */
    public String updateDigestValue() { return updateDigestValue; }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntryNameAndTwoDigestValues)) {
            return false;
        }
        final EntryNameAndTwoDigestValues that = (EntryNameAndTwoDigestValues) obj;
        return  this.name().equals(that.name()) &&
                this.baseDigestValue().equals(that.baseDigestValue()) &&
                this.updateDigestValue().equals(that.updateDigestValue());
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + name().hashCode();
        hash = 31 * hash + baseDigestValue().hashCode();
        hash = 31 * hash + updateDigestValue().hashCode();
        return hash;
    }
}
