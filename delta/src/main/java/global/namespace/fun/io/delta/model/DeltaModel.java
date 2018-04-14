/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * A Value Object which represents the meta data in a delta-archive file.
 * It encapsulates unmodifiable collections of changed, unchanged, added and
 * removed entry names and message digests in canonical string notation,
 * attributed with the message digest algorithm name and byte length.
 *
 * @author Christian Schlichtherle
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
public final class DeltaModel {

    private final String digestAlgorithmName;

    private final Optional<Integer> digestByteLength;

    private final Map<String, EntryNameAndTwoDigestValues> changed;

    private final Map<String, EntryNameAndDigestValue> unchanged, added, removed;

    private DeltaModel(final Builder b) {
        final MessageDigest digest = b.messageDigest.get();
        this.digestAlgorithmName = digest.getAlgorithm();
        this.digestByteLength = lengthBytes(digest);
        this.changed = changedMap(b.changed);
        this.unchanged = unchangedMap(b.unchanged);
        this.added = unchangedMap(b.added);
        this.removed = unchangedMap(b.removed);
    }

    /** Returns a new builder for a delta model. */
    public static Builder builder() { return new Builder(); }

    private static Optional<Integer> lengthBytes(final MessageDigest digest) {
        final MessageDigest clone;
        try {
            clone = MessageDigest.getInstance(digest.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        if (clone.getDigestLength() == digest.getDigestLength()) {
            return empty();
        } else {
            return of(digest.getDigestLength());
        }
    }

    private static Map<String, EntryNameAndTwoDigestValues> changedMap(final Collection<EntryNameAndTwoDigestValues> entries) {
        final Map<String, EntryNameAndTwoDigestValues> map = new LinkedHashMap<>(initialCapacity(entries));
        for (EntryNameAndTwoDigestValues entryNameAndTwoDigestValues : entries) {
            map.put(entryNameAndTwoDigestValues.name(), entryNameAndTwoDigestValues);
        }
        return unmodifiableMap(map);
    }

    private static Map<String, EntryNameAndDigestValue> unchangedMap(final Collection<EntryNameAndDigestValue> entries) {
        final Map<String, EntryNameAndDigestValue> map = new LinkedHashMap<>(initialCapacity(entries));
        for (EntryNameAndDigestValue entryNameAndDigestValue : entries) {
            map.put(entryNameAndDigestValue.name(), entryNameAndDigestValue);
        }
        return unmodifiableMap(map);
    }

    private static int initialCapacity(Collection<?> c) { return HashMaps.initialCapacity(c.size()); }

    /** Returns the message digest algorithm name. */
    public String digestAlgorithmName() { return digestAlgorithmName; }

    /**
     * Returns the message digest byte length.
     * This is empty if and only if the byte length of the message digest used to build this delta model is the default
     * value for the algorithm.
     */
    public Optional<Integer> digestByteLength() { return digestByteLength; }

    /**
     * Returns a collection of the entry name and two message digests for the
     * <i>changed</i> entries.
     */
    public Collection<EntryNameAndTwoDigestValues> changedEntries() { return changed.values(); }

    /** Looks up the given entry name in the <i>changed</i> entries. */
    public EntryNameAndTwoDigestValues changed(String name) { return changed.get(name); }

    /**
     * Returns a collection of the entry name and message digest for the
     * <i>unchanged</i> entries.
     */
    public Collection<EntryNameAndDigestValue> unchangedEntries() { return unchanged.values(); }

    /** Looks up the given entry name in the <i>unchanged</i> entries. */
    @Deprecated
    public EntryNameAndDigestValue unchanged(String name) { return unchanged.get(name); }

    /**
     * Returns a collection of the entry name and message digest for the
     * <i>added</i> entries.
     */
    public Collection<EntryNameAndDigestValue> addedEntries() { return added.values(); }

    /** Looks up the given entry name in the <i>added</i> entries. */
    public EntryNameAndDigestValue added(String name) { return added.get(name); }

    /**
     * Returns a collection of the entry name and message digest for the
     * <i>removed</i> entries.
     */
    public Collection<EntryNameAndDigestValue> removedEntries() { return removed.values(); }

    /** Looks up the given entry name in the <i>removed</i> entries. */
    @Deprecated
    public EntryNameAndDigestValue removed(String name) { return removed.get(name); }

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DeltaModel)) {
            return false;
        }
        final DeltaModel that = (DeltaModel) obj;
        return  this.digestAlgorithmName.equals(that.digestAlgorithmName) &&
                this.digestByteLength.equals(that.digestByteLength) &&
                this.changed.equals(that.changed) &&
                this.unchanged.equals(that.unchanged) &&
                this.added.equals(that.added) &&
                this.removed.equals(that.removed);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + digestAlgorithmName.hashCode();
        hash = 31 * hash + digestByteLength.hashCode();
        hash = 31 * hash + changed.hashCode();
        hash = 31 * hash + unchanged.hashCode();
        hash = 31 * hash + added.hashCode();
        hash = 31 * hash + removed.hashCode();
        return hash;
    }

    /**
     * A builder for a delta model.
     * The default value for the collection of <i>unchanged</i>, <i>changed</i>,
     * <i>added</i> and <i>removed</i> entry names and message digests is an
     * empty collection.
     */
    public static final class Builder {

        private Optional<MessageDigest> messageDigest = empty();
        private Collection<EntryNameAndTwoDigestValues> changed = emptyList();
        private Collection<EntryNameAndDigestValue> unchanged = emptyList(), added = emptyList(), removed = emptyList();

        private Builder() { }

        public Builder messageDigest(final MessageDigest messageDigest) {
            this.messageDigest = of(messageDigest);
            return this;
        }

        public Builder changedEntries(final Collection<EntryNameAndTwoDigestValues> changed) {
            this.changed = requireNonNull(changed);
            return this;
        }

        public Builder unchangedEntries(final Collection<EntryNameAndDigestValue> unchanged) {
            this.unchanged = requireNonNull(unchanged);
            return this;
        }

        public Builder addedEntries(final Collection<EntryNameAndDigestValue> added) {
            this.added = requireNonNull(added);
            return this;
        }

        public Builder removedEntries(final Collection<EntryNameAndDigestValue> removed) {
            this.removed = requireNonNull(removed);
            return this;
        }

        public DeltaModel build() { return new DeltaModel(this); }
    }
}
