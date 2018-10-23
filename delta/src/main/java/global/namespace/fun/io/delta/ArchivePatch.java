/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import global.namespace.fun.io.api.*;
import global.namespace.fun.io.api.function.XConsumer;
import global.namespace.fun.io.delta.model.DeltaModel;
import global.namespace.fun.io.delta.model.EntryNameAndDigestValue;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static global.namespace.fun.io.delta.Delta.decodeModel;
import static java.util.Arrays.asList;

/**
 * Patches a base archive file to an update archive file using a delta archive file.
 *
 * @author Christian Schlichtherle
 */
abstract class ArchivePatch {

    abstract ArchiveSource baseSource();

    abstract ArchiveSource deltaSource();

    void to(ArchiveSink update) throws Exception {
        this.accept(engine -> update.acceptWriter(engine::to));
    }

    private void accept(XConsumer<Engine> consumer) throws Exception {
        baseSource().acceptReader(baseInput -> deltaSource().acceptReader(deltaInput -> consumer.accept(
                new Engine() {

                    ArchiveInputStream baseInput() {
                        return baseInput;
                    }

                    ArchiveInputStream deltaInput() {
                        return deltaInput;
                    }
                }
        )));
    }

    private abstract class Engine {

        DeltaModel model;

        WithMessageDigest digest;

        abstract ArchiveInputStream baseInput();

        abstract ArchiveInputStream deltaInput();

        void to(final ArchiveOutputStream updateOutput) throws Exception {
            for (Predicate<String> filter : passFilters(updateOutput)) {
                to(updateOutput, filter);
            }
        }

        /**
         * Returns a list of filters for the different passes required to generate the update archive file.
         * At least one filter is required to output anything.
         * The filters should properly partition the set of entry sources, i.e. each entry source should be accepted by
         * exactly one filter.
         */
        Iterable<Predicate<String>> passFilters(final ArchiveOutputStream updateOutput) {
            if (updateOutput.isJar()) {
                // java.util.JarInputStream assumes that the file entry
                // "META-INF/MANIFEST.MF" should either be the first or the second
                // entry (if preceded by the directory entry "META-INF/"), so we
                // need to process the delta-archive file in two passes with a
                // corresponding filter to ensure this order.
                // Note that the directory entry "META-INF/" is always part of the
                // unchanged delta set because it's content is always empty.
                // Thus, by copying the unchanged entries before the changed
                // entries, the directory entry "META-INF/" will always appear
                // before the file entry "META-INF/MANIFEST.MF".
                final Predicate<String> manifestFilter = "META-INF/MANIFEST.MF"::equals;
                return asList(manifestFilter, manifestFilter.negate());
            } else {
                return Collections.singletonList(t -> true);
            }
        }

        void to(final ArchiveOutputStream updateOutput, final Predicate<String> filter) throws Exception {

            abstract class Patch {

                abstract ArchiveInputStream input();

                abstract IOException ioException(Throwable cause);

                final void apply(final Collection<EntryNameAndDigestValue> collection) throws Exception {
                    for (final EntryNameAndDigestValue entryNameAndDigestValue : collection) {
                        final String name = entryNameAndDigestValue.name();
                        if (filter.test(name)) {
                            final Optional<ArchiveEntrySource> optEntry = input().source(name);
                            if (optEntry.isPresent()) {
                                final ArchiveEntrySource entry = optEntry.get();
                                if (!digestValueOf(entry).equals(entryNameAndDigestValue.digestValue())) {
                                    throw ioException(new WrongMessageDigestException(name));
                                }
                                entry.copyTo(updateOutput.sink(name));
                            } else {
                                throw ioException(new MissingArchiveEntryException(name));
                            }
                        }
                    }
                }
            }

            class OnBaseInputPatch extends Patch {

                @Override
                ArchiveInputStream input() {
                    return baseInput();
                }

                @Override
                IOException ioException(Throwable cause) {
                    return new WrongBaseArchiveFileException(cause);
                }
            }

            class OnDeltaInputPatch extends Patch {

                @Override
                ArchiveInputStream input() {
                    return deltaInput();
                }

                @Override
                IOException ioException(Throwable cause) {
                    return new InvalidDeltaArchiveFileException(cause);
                }
            }

            // Order is important here!
            new OnBaseInputPatch().apply(model().unchangedEntries());
            new OnDeltaInputPatch().apply(model().changedEntries()
                    .stream()
                    .map(change -> new EntryNameAndDigestValue(change.name(), change.updateDigestValue()))
                    .collect(Collectors.toList()));
            new OnDeltaInputPatch().apply(model().addedEntries());
        }

        String digestValueOf(Source source) throws Exception {
            return digest().digestValueOf(source);
        }

        WithMessageDigest digest() throws Exception {
            final WithMessageDigest digest = this.digest;
            return null != digest
                    ? digest
                    : (this.digest = WithMessageDigest.of(MessageDigest.getInstance(model().digestAlgorithmName())));
        }

        DeltaModel model() throws Exception {
            final DeltaModel model = this.model;
            return null != model ? model : (this.model = decodeModel(deltaInput()));
        }
    }
}
