/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.model;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * A Value Object which represents a ZIP entry name and two message digests in
 * canonical string notation.
 *
 * @author Christian Schlichtherle
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public final class EntryNameAndTwoDigests implements Serializable {

    private static final long serialVersionUID = 0L;

    @XmlAttribute(required = true)
    private final String name, first, second;

    /** Required for JAXB. */
    private EntryNameAndTwoDigests() {
        name = first = second = "";
    }

    /**
     * Default constructor.
     * The first and second message digest should not be equal.
     */
    public EntryNameAndTwoDigests(
            final String name,
            final String first,
            final String second) {
        this.name = requireNonNull(name);
        this.first = requireNonNull(first);
        this.second = requireNonNull(second);
        assert !first.equals(second);
    }

    /** Returns the entry name. */
    public String name() { return name; }

    /** Returns the first message digest value. */
    public String first() { return first; }

    /** Returns the second message digest value. */
    public String second() { return second; }

    /** Returns the first ZIP entry name and digest value. */
    @Deprecated
    public EntryNameAndDigest entryNameAndDigest1() {
        return new EntryNameAndDigest(name(), first());
    }

    /** Returns the second ZIP entry name and digest value. */
    public EntryNameAndDigest entryNameAndDigest2() {
        return new EntryNameAndDigest(name(), second());
    }

    @Override public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntryNameAndTwoDigests)) return false;
        final EntryNameAndTwoDigests that = (EntryNameAndTwoDigests) obj;
        return  this.name().equals(that.name()) &&
                this.first().equals(that.first()) &&
                this.second().equals(that.second());
    }

    @Override public int hashCode() {
        int hash = 17;
        hash = 31 * hash + name().hashCode();
        hash = 31 * hash + first().hashCode();
        hash = 31 * hash + second().hashCode();
        return hash;
    }
}
