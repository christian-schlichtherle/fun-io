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

    public abstract void outputTo(ZipSink sink) throws Exception;

    /**
     * A builder for a ZIP diff.
     * The default message digest is SHA-1.
     */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
    public static class Builder {

        private Optional<ZipSource> source1 = empty(), source2 = empty();
        private Optional<String> digest = empty();

        Builder() { }

        public Builder source1(final ZipSource source1) {
            this.source1 = Optional.of(source1);
            return this;
        }

        public Builder source2(final ZipSource source2) {
            this.source2 = Optional.of(source2);
            return this;
        }

        public Builder digest(final String digest) {
            this.digest = Optional.of(digest);
            return this;
        }

        public ZipDiff build() { return create(source1.get(), source2.get(), digest); }

        private static ZipDiff create(final ZipSource source1, final ZipSource source2, final Optional<String> digestName) {
            return new ZipDiff() {

                @Override
                public void outputTo(final ZipSink sink) throws Exception {
                    source1.acceptReader(input1 ->
                            source2.acceptReader(input2 ->
                                    sink.acceptWriter(delta ->
                                            new ZipDiffEngine() {

                                                final MessageDigest digest =
                                                        MessageDigests.create(digestName.orElse("SHA-1"));

                                                protected MessageDigest digest() { return digest; }

                                                protected ZipInput input1() { return input1; }

                                                protected ZipInput input2() { return input2; }
                                            }.output(delta)
                                    )
                            )
                    );
                }
            };
        }
    }
}
