/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import global.namespace.fun.io.api.*;
import global.namespace.fun.io.api.function.XFunction;
import global.namespace.fun.io.delta.model.DeltaModel;
import global.namespace.fun.io.delta.model.EntryNameAndDigestValue;
import global.namespace.fun.io.delta.model.EntryNameAndTwoDigestValues;

import java.security.MessageDigest;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static global.namespace.fun.io.delta.Delta.encodeModel;

/**
 * Compares a base archive file to an update archive file and generates a delta archive file.
 *
 * @author Christian Schlichtherle
 */
abstract class ArchiveDiff implements WithMessageDigest {

    public abstract MessageDigest digest();

    abstract ArchiveSource baseSource();

    abstract ArchiveSource updateSource();

    DeltaModel toModel() throws Exception {
        return apply(Engine::toModel);
    }

    void to(ArchiveSink delta) throws Exception {
        this.apply(engine -> {
            delta.acceptWriter(engine::to);
            return null;
        });
    }

    private <T> T apply(XFunction<Engine, T> function) throws Exception {
        return baseSource().applyReader(baseInput -> updateSource().applyReader(updateInput -> function.apply(
                new Engine() {

                    ArchiveInputStream baseInput() {
                        return baseInput;
                    }

                    ArchiveInputStream updateInput() {
                        return updateInput;
                    }
                }
        )));
    }

    private abstract class Engine {

        abstract ArchiveInputStream baseInput();

        abstract ArchiveInputStream updateInput();

        void to(final ArchiveOutputStream deltaOutput) throws Exception {

            final class Streamer {

                private final DeltaModel model = toModel();

                private Streamer() throws Exception {
                    encodeModel(deltaOutput, model);
                }

                private void stream() throws Exception {
                    for (final ArchiveEntrySource updateEntry : updateInput()) {
                        final String name = updateEntry.name();
                        if (changedOrAdded(name)) {
                            updateEntry.copyTo(deltaOutput.sink(name));
                        }
                    }
                }

                private boolean changedOrAdded(String name) {
                    return null != model.changed(name) || null != model.added(name);
                }
            }

            new Streamer().stream();
        }

        DeltaModel toModel() throws Exception {
            final Assembly assembly = new Assembly();

            for (final ArchiveEntrySource baseEntry : baseInput()) {
                if (!baseEntry.directory()) {
                    final Optional<ArchiveEntrySource> updateEntry = updateInput().source(baseEntry.name());
                    if (updateEntry.isPresent()) {
                        assembly.visitEntriesInBothFiles(baseEntry, updateEntry.get());
                    } else {
                        assembly.visitEntryInBaseFile(baseEntry);
                    }
                }
            }

            for (final ArchiveEntrySource updateEntry : updateInput()) {
                if (!updateEntry.directory()) {
                    final Optional<ArchiveEntrySource> baseEntry = baseInput().source(updateEntry.name());
                    if (!baseEntry.isPresent()) {
                        assembly.visitEntryInUpdateFile(updateEntry);
                    }
                }
            }

            return assembly.deltaModel();
        }

        /**
         * A visitor of two archive files.
         * Note that the order of the calls to the visitor methods is undefined, so you should not depend on the
         * behavior of the current implementation in order to ensure compatibility with future versions.
         */
        class Assembly {

            final Map<String, EntryNameAndTwoDigestValues> changed = new TreeMap<>();

            final Map<String, EntryNameAndDigestValue>
                    unchanged = new TreeMap<>(),
                    added = new TreeMap<>(),
                    removed = new TreeMap<>();

            DeltaModel deltaModel() {
                return DeltaModel
                        .builder()
                        .messageDigest(digest())
                        .changedEntries(changed.values())
                        .unchangedEntries(unchanged.values())
                        .addedEntries(added.values())
                        .removedEntries(removed.values())
                        .build();
            }

            /**
             * Visits a pair of archive entries with equal names in the base and update archive file.
             *
             * @param baseEntry   the source for reading the archive entry in the base archive file.
             * @param updateEntry the source for reading the archive entry in the update archive file.
             */
            void visitEntriesInBothFiles(final ArchiveEntrySource baseEntry,
                                         final ArchiveEntrySource updateEntry)
                    throws Exception {
                final String name = baseEntry.name();
                assert name.equals(updateEntry.name());
                final String baseValue = digestValueOf(baseEntry);
                final String updateValue = digestValueOf(updateEntry);
                if (baseValue.equals(updateValue)) {
                    unchanged.put(name, new EntryNameAndDigestValue(name, baseValue));
                } else {
                    changed.put(name, new EntryNameAndTwoDigestValues(name, baseValue, updateValue));
                }
            }

            /**
             * Visits an archive entry which is present in the base archive file, but not in the update archive file.
             *
             * @param baseEntry the source for reading the archive entry in the base archive file.
             */
            void visitEntryInBaseFile(final ArchiveEntrySource baseEntry) throws Exception {
                final String name = baseEntry.name();
                removed.put(name, new EntryNameAndDigestValue(name, digestValueOf(baseEntry)));
            }

            /**
             * Visits an archive entry which is present in the update archive file, but not in the base archive file.
             *
             * @param updateEntry the source for reading the archive entry in the update archive file.
             */
            void visitEntryInUpdateFile(final ArchiveEntrySource updateEntry) throws Exception {
                final String name = updateEntry.name();
                added.put(name, new EntryNameAndDigestValue(name, digestValueOf(updateEntry)));
            }
        }
    }
}
