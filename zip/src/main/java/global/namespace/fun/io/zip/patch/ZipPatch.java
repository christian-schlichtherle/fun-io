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

    public abstract void outputTo(ZipSink update) throws Exception;

    /** A builder for a ZIP patch. */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
    public static class Builder {

        private Optional<ZipSource> base = empty(), patch = empty();

        Builder() { }

        public Builder base(final ZipSource base) {
            this.base = Optional.of(base);
            return this;
        }

        public Builder patch(final ZipSource patch) {
            this.patch = Optional.of(patch);
            return this;
        }

        public ZipPatch build() { return create(base.get(), patch.get()); }

        private static ZipPatch create(final ZipSource baseSource, final ZipSource patchSource) {
            return new ZipPatch() {

                @Override
                public void outputTo(final ZipSink updateSink) throws Exception {
                    baseSource.acceptReader(base ->
                            patchSource.acceptReader(patch ->
                                    updateSink.acceptWriter(update ->
                                            new ZipPatchEngine() {

                                                protected ZipInput base() { return base; }

                                                protected ZipInput patch() { return patch; }
                                            }.outputTo(update)
                                    )
                            )
                    );
                }
            };
        }
    }
}
