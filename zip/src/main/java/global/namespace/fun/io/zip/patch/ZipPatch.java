/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.patch;

import global.namespace.fun.io.zip.io.ZipInput;
import global.namespace.fun.io.zip.io.ZipSink;
import global.namespace.fun.io.zip.io.ZipSource;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

/**
 * Applies a delta ZIP file to an input archive and generates an output archive.
 * Archives may be ZIP, JAR, EAR or WAR files.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public abstract class ZipPatch {

    /** Returns a new builder for a ZIP patch. */
    public static Builder builder() { return new Builder(); }

    public abstract void outputTo(ZipSink sink) throws Exception;

    /** A builder for a ZIP patch. */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
    public static class Builder {

        private Optional<ZipSource> source = empty(), delta = empty();

        Builder() { }

        public Builder source(final ZipSource source) {
            this.source = Optional.of(source);
            return this;
        }

        public Builder delta(final ZipSource delta) {
            this.delta = Optional.of(delta);
            return this;
        }

        public ZipPatch build() { return create(source.get(), delta.get()); }

        private static ZipPatch create(final ZipSource baseSource, final ZipSource deltaSource) {
            return new ZipPatch() {

                @Override
                public void outputTo(final ZipSink sink) throws Exception {
                    baseSource.acceptReader(input ->
                            deltaSource.acceptReader(delta ->
                                    sink.acceptWriter(output ->
                                            new ZipPatchEngine() {

                                                protected ZipInput input() { return input; }

                                                protected ZipInput delta() { return delta; }
                                            }.output(output)
                                    )
                            )
                    );
                }
            };
        }
    }
}
