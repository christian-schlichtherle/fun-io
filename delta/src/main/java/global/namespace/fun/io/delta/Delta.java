/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import global.namespace.fun.io.api.*;
import global.namespace.fun.io.delta.dto.DeltaDTO;
import global.namespace.fun.io.delta.dto.EntryNameAndDigestValueDTO;
import global.namespace.fun.io.delta.dto.EntryNameAndTwoDigestValuesDTO;
import global.namespace.fun.io.delta.model.DeltaModel;
import global.namespace.fun.io.delta.model.EntryNameAndDigestValue;
import global.namespace.fun.io.delta.model.EntryNameAndTwoDigestValues;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static global.namespace.fun.io.jackson.Jackson.json;
import static java.util.Collections.emptyList;

/**
 * Diffs and patches archive files.
 *
 * @author Christian Schlichtherle
 */
public final class Delta {

    private Delta() { }

    private static final String META_INF_DELTA_JSON = "META-INF/delta.json";

    /**
     * Returns a builder for comparing a base archive file to an update archive file and generating a delta archive
     * file.
     */
    public static ArchiveDiffBuilder diff() { return new ArchiveDiffBuilder(); }

    /** Returns a builder for patching a base archive file with a delta archive file to an update archive file. */
    public static ArchivePatchBuilder patch() { return new ArchivePatchBuilder(); }

    static <E> void encodeModel(ArchiveOutput<E> output, DeltaModel model) throws Exception {
        encodeModel(output.sink(META_INF_DELTA_JSON), model);
    }

    static <E> DeltaModel decodeModel(ArchiveInput<E> input) throws Exception {
        return decodeModel(input.source(META_INF_DELTA_JSON).orElseThrow(() ->
                new InvalidDeltaArchiveFileException(new MissingArchiveEntryException(META_INF_DELTA_JSON))));
    }

    /** Encodes the given delta model to the given sink. */
    static void encodeModel(Sink sink, DeltaModel model) throws Exception { encodeDTO(sink, marshal(model)); }

    /** Decodes a delta model from the given source. */
    static DeltaModel decodeModel(Source source) throws Exception { return unmarshal(decodeDTO(source)); }

    private static void encodeDTO(Sink sink, DeltaDTO dto) throws Exception { jsonCodec().encoder(sink).encode(dto); }

    private static DeltaDTO decodeDTO(Source source) throws Exception {
        return jsonCodec().decoder(source).decode(DeltaDTO.class);
    }

    private static Codec jsonCodec() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        return json(mapper);
    }

    private static DeltaDTO marshal(final DeltaModel model) {
        if (null == model) {
            return null;
        } else {
            final DeltaDTO dto = new DeltaDTO();
            dto.algorithm = model.digestAlgorithmName();
            dto.numBytes = model.digestByteLength().orElse(0);
            dto.changed = marshal2(model.changedEntries());
            dto.unchanged = marshal(model.unchangedEntries());
            dto.added = marshal(model.addedEntries());
            dto.removed = marshal(model.removedEntries());
            return dto;
        }
    }

    private static DeltaModel unmarshal(final DeltaDTO dto) throws Exception {
        if (null == dto) {
            return null;
        } else {
            return DeltaModel
                    .builder()
                    .messageDigest(MessageDigest.getInstance(dto.algorithm))
                    .changedEntries(unmarshal2(dto.changed))
                    .unchangedEntries(unmarshal(dto.unchanged))
                    .addedEntries(unmarshal(dto.added))
                    .removedEntries(unmarshal(dto.removed))
                    .build();
        }
    }

    private static EntryNameAndTwoDigestValuesDTO[] marshal2(final Collection<EntryNameAndTwoDigestValues> c) {
        if (null == c || c.isEmpty()) {
            return null;
        } else {
            return c.stream().map(values -> {
                final EntryNameAndTwoDigestValuesDTO dto = new EntryNameAndTwoDigestValuesDTO();
                dto.name = values.name();
                dto.first = values.baseDigestValue();
                dto.second = values.updateDigestValue();
                return dto;
            }).toArray(EntryNameAndTwoDigestValuesDTO[]::new);
        }
    }

    private static List<EntryNameAndTwoDigestValues> unmarshal2(EntryNameAndTwoDigestValuesDTO[] c) {
        return null == c ? emptyList() : Arrays.stream(c)
                .map(dto -> new EntryNameAndTwoDigestValues(dto.name, dto.first, dto.second))
                .collect(Collectors.toList());
    }

    private static EntryNameAndDigestValueDTO[] marshal(final Collection<EntryNameAndDigestValue> c) {
        if (null == c || c.isEmpty()) {
            return null;
        } else {
            return c.stream().map(value -> {
                final EntryNameAndDigestValueDTO dto = new EntryNameAndDigestValueDTO();
                dto.name = value.name();
                dto.digest = value.digestValue();
                return dto;
            }).toArray(EntryNameAndDigestValueDTO[]::new);
        }
    }

    private static List<EntryNameAndDigestValue> unmarshal(EntryNameAndDigestValueDTO[] c) {
        return null == c ? emptyList() : Arrays.stream(c)
                .map(dto -> new EntryNameAndDigestValue(dto.name, dto.digest))
                .collect(Collectors.toList());
    }
}
