/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta;

import global.namespace.fun.io.delta.model.DeltaModel;
import global.namespace.fun.io.delta.model.EntryNameAndDigestValue;
import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.archive.*;
import global.namespace.fun.io.api.function.XConsumer;

import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static global.namespace.fun.io.delta.Delta.decodeModel;
import static global.namespace.fun.io.delta.MessageDigests.valueOf;
import static global.namespace.fun.io.bios.BIOS.copy;
import static java.util.Arrays.asList;

/**
 * Patches a base archive file to an update archive file using a delta archive file.
 *
 * @author Christian Schlichtherle
 */
abstract class ArchiveFilePatch<F, D, S> {

    abstract ArchiveFileSource<F> baseSource();

    abstract ArchiveFileSource<D> deltaSource();

    void to(ArchiveFileSink<S> update) throws Exception {
        accept(engine -> update.acceptWriter(engine::to));
    }

    private void accept(final XConsumer<Engine> consumer) throws Exception {
        baseSource().acceptReader(baseInput -> deltaSource().acceptReader(deltaInput -> consumer.accept(
                new Engine() {

                    ArchiveFileInput<F> baseInput() { return baseInput; }

                    ArchiveFileInput<D> deltaInput() { return deltaInput; }
                }
        )));
    }

    private abstract class Engine {

        DeltaModel model;

        abstract ArchiveFileInput<F> baseInput();

        abstract ArchiveFileInput<D> deltaInput();

        void to(final ArchiveFileOutput<S> updateOutput) throws Exception {
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
        Iterable<Predicate<String>> passFilters(final ArchiveFileOutput<S> updateOutput) {
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

        void to(final ArchiveFileOutput<S> updateOutput, final Predicate<String> filter) throws Exception {

            class MyArchiveEntrySink implements Sink {

                private final EntryNameAndDigestValue entryNameAndDigest;

                MyArchiveEntrySink(final EntryNameAndDigestValue entryNameAndDigest) {
                    assert null != entryNameAndDigest;
                    this.entryNameAndDigest = entryNameAndDigest;
                }

                @Override
                public Socket<OutputStream> output() {
                    return updateOutput.sink(entryNameAndDigest.name()).output().map(out -> {
                        final MessageDigest digest = digest();
                        digest.reset();
                        return new DigestOutputStream(out, digest) {

                            @Override
                            public void close() throws IOException {
                                super.close();
                                if (!valueOfDigest().equals(entryNameAndDigest.digestValue())) {
                                    throw new WrongMessageDigestException(entryNameAndDigest.name());
                                }
                            }

                            String valueOfDigest() { return valueOf(digest); }
                        };
                    });
                }

            }

            abstract class Patch<E> {

                abstract ArchiveFileInput<E> input();

                abstract IOException ioException(Throwable cause);

                final void apply(final Collection<EntryNameAndDigestValue> collection) throws Exception {
                    for (final EntryNameAndDigestValue entryNameAndDigestValue : collection) {
                        final String name = entryNameAndDigestValue.name();
                        if (filter.test(name)) {
                            final Optional<ArchiveEntrySource<E>> entry = input().source(name);
                            try {
                                copy(
                                        entry.orElseThrow(() -> ioException(new MissingArchiveEntryException(name))),
                                        new MyArchiveEntrySink(entryNameAndDigestValue)
                                );
                            } catch (WrongMessageDigestException e) {
                                throw ioException(e);
                            }
                        }
                    }
                }
            }

            class OnBaseInputPatch extends Patch<F> {

                @Override
                ArchiveFileInput<F> input() { return baseInput(); }

                @Override
                IOException ioException(Throwable cause) { return new WrongBaseArchiveFileException(cause); }
            }

            class OnDeltaInputPatch extends Patch<D> {

                @Override
                ArchiveFileInput<D> input() { return deltaInput(); }

                @Override
                IOException ioException(Throwable cause) { return new InvalidDeltaArchiveFileException(cause); }
            }

            // Order is important here!
            new OnBaseInputPatch().apply(model().unchangedEntries());
            new OnDeltaInputPatch().apply(model().changedEntries()
                    .stream()
                    .map(change -> new EntryNameAndDigestValue(change.name(), change.updateDigestValue()))
                    .collect(Collectors.toList()));
            new OnDeltaInputPatch().apply(model().addedEntries());
        }

        MessageDigest digest() throws Exception { return MessageDigest.getInstance(model().digestAlgorithmName()); }

        DeltaModel model() throws Exception {
            final DeltaModel model = this.model;
            return null != model ? model : (this.model = decodeModel(deltaInput()));
        }
    }
}
