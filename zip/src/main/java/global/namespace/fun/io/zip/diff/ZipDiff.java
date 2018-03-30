/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.diff;

import global.namespace.fun.io.zip.io.MessageDigests;
import global.namespace.fun.io.zip.io.ZipInput;
import global.namespace.fun.io.zip.io.ZipSink;
import global.namespace.fun.io.zip.io.ZipSource;

import java.security.MessageDigest;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * Compares two archives entry by entry.
 * Archives may be ZIP, JAR, EAR or WAR files.
 *
 * @author Christian Schlichtherle
 */
public abstract class ZipDiff {

    /** Returns a new builder for a ZIP diff. */
    public static Builder builder() { return new Builder(); }

    public abstract void outputTo(ZipSink patch) throws Exception;

    /**
     * A builder for a ZIP diff.
     * The default message digest is SHA-1.
     */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
    public static class Builder {

        private Optional<String> digest = empty();
        private Optional<ZipSource> base = empty(), update = empty();

        Builder() { }

        public Builder digest(final String digest) {
            this.digest = Optional.of(digest);
            return this;
        }

        public Builder base(final ZipSource base) {
            this.base = Optional.of(base);
            return this;
        }

        public Builder update(final ZipSource update) {
            this.update = Optional.of(update);
            return this;
        }

        public ZipDiff build() { return create(digest, base.get(), update.get()); }

        private static ZipDiff create(final Optional<String> digestName, final ZipSource baseSource, final ZipSource updateSource) {
            return new ZipDiff() {

                @Override
                public void outputTo(final ZipSink patchSink) throws Exception {
                    baseSource.acceptReader(base ->
                            updateSource.acceptReader(update ->
                                    patchSink.acceptWriter(patch ->
                                            new ZipDiffEngine() {

                                                final MessageDigest digest =
                                                        MessageDigests.create(digestName.orElse("SHA-1"));

                                                protected MessageDigest digest() { return digest; }

                                                protected ZipInput base() { return base; }

                                                protected ZipInput update() { return update; }
                                            }.outputTo(patch)
                                    )
                            )
                    );
                }
            };
        }
    }
}
