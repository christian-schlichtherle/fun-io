/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Collection;
import java.util.Map;

@Immutable
final class EntryNameAndDigestMapAdapter
extends XmlAdapter<EntryNameAndDigestCollectionDto,
                   Map<String, EntryNameAndDigest>> {

    @Override public @Nullable Map<String, EntryNameAndDigest> unmarshal(
            @CheckForNull EntryNameAndDigestCollectionDto dto) {
        return null == dto ? null : DeltaModel.unchangedMap(dto.entries);
    }

    @Override public @Nullable EntryNameAndDigestCollectionDto marshal(
            final @CheckForNull Map<String, EntryNameAndDigest> map) {
        if (null == map || map.isEmpty()) return null;
        final EntryNameAndDigestCollectionDto
                dto = new EntryNameAndDigestCollectionDto();
        dto.entries = map.values();
        return dto;
    }
}

final class EntryNameAndDigestCollectionDto {
    @XmlElement(name = "entry")
    public Collection<EntryNameAndDigest> entries;
}

@Immutable
final class EntryNameAndTwoDigestsMapAdapter
extends XmlAdapter<EntryNameAndTwoDigestsCollectionDto,
                   Map<String, EntryNameAndTwoDigests>> {

    @Override public @Nullable Map<String, EntryNameAndTwoDigests> unmarshal(
            @CheckForNull EntryNameAndTwoDigestsCollectionDto dto) {
        return null == dto ? null : DeltaModel.changedMap(dto.entries);
    }

    @Override public @Nullable EntryNameAndTwoDigestsCollectionDto marshal(
            final @CheckForNull Map<String, EntryNameAndTwoDigests> map) {
        if (null == map || map.isEmpty()) return null;
        final EntryNameAndTwoDigestsCollectionDto
                dto = new EntryNameAndTwoDigestsCollectionDto();
        dto.entries = map.values();
        return dto;
    }
}

final class EntryNameAndTwoDigestsCollectionDto {
    @XmlElement(name = "entry")
    public Collection<EntryNameAndTwoDigests> entries;
}
