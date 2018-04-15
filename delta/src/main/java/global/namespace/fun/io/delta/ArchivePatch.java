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
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static global.namespace.fun.io.bios.BIOS.copy;
import static global.namespace.fun.io.delta.Delta.decodeModel;
import static global.namespace.fun.io.delta.MessageDigests.valueOf;
import static java.util.Arrays.asList;

/**
 * Patches a base archive file to an update archive file using a delta archive file.
 *
 * @author Christian Schlichtherle
 */
abstract class ArchivePatch<F, D, S> {

    abstract ArchiveSource<F> baseSource();

    abstract ArchiveSource<D> deltaSource();

    void to(ArchiveSink<S> update) throws Exception {
        accept(engine -> update.acceptWriter(engine::to));
    }

    private void accept(final XConsumer<Engine> consumer) throws Exception {
        baseSource().acceptReader(baseInput -> deltaSource().acceptReader(deltaInput -> consumer.accept(
                new Engine() {

                    ArchiveInput<F> baseInput() { return baseInput; }

                    ArchiveInput<D> deltaInput() { return deltaInput; }
                }
        )));
    }

    private abstract class Engine {

        DeltaModel model;

        abstract ArchiveInput<F> baseInput();

        abstract ArchiveInput<D> deltaInput();

        void to(final ArchiveOutput<S> updateOutput) throws Exception {
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
        Iterable<Predicate<String>> passFilters(final ArchiveOutput<S> updateOutput) {
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

        void to(final ArchiveOutput<S> updateOutput, final Predicate<String> filter) throws Exception {

            abstract class Patch<E> {

                abstract ArchiveInput<E> input();

                abstract IOException ioException(Throwable cause);

                final void apply(final Collection<EntryNameAndDigestValue> collection) throws Exception {
                    for (final EntryNameAndDigestValue entryNameAndDigestValue : collection) {
                        final String name = entryNameAndDigestValue.name();
                        if (filter.test(name)) {

                            // TODO: Maybe this digest check should not be inserted like a transformation in the copying
                            // of the archive entries because (a) if the check fails then the entry is already copied,
                            // so the compromised data has already been written and (b) the inserting disables taking
                            // advantage of the potentially more efficient
                            // `ArchiveEntrySource.copyTo(ArchiveEntrySink)`, e.g. with `fun-io-commons-compress`.
                            class CheckDigestTransformation implements Transformation {

                                public Socket<OutputStream> apply(Socket<OutputStream> output) {
                                    return output.map(out -> {
                                        final MessageDigest digest = digest();
                                        digest.reset();
                                        return new DigestOutputStream(out, digest) {

                                            @Override
                                            public void close() throws IOException {
                                                super.close();
                                                if (!valueOfDigest().equals(entryNameAndDigestValue.digestValue())) {
                                                    throw new WrongMessageDigestException(name);
                                                }
                                            }

                                            String valueOfDigest() { return valueOf(digest); }
                                        };
                                    });
                                }

                                public Socket<InputStream> unapply(Socket<InputStream> input) { throw new UnsupportedOperationException(); }

                                public Transformation inverse() { throw new UnsupportedOperationException(); }
                            }

                            final Optional<ArchiveEntrySource<E>> entry = input().source(name);
                            try {
                                copy(
                                        entry.orElseThrow(() -> ioException(new MissingArchiveEntryException(name))),
                                        updateOutput.sink(name).map(new CheckDigestTransformation())
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
                ArchiveInput<F> input() { return baseInput(); }

                @Override
                IOException ioException(Throwable cause) { return new WrongBaseArchiveFileException(cause); }
            }

            class OnDeltaInputPatch extends Patch<D> {

                @Override
                ArchiveInput<D> input() { return deltaInput(); }

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
