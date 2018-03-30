/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.zip.diff;

import edu.umd.cs.findbugs.annotations.CreatesObligation;
import global.namespace.fun.io.zip.io.MessageDigests;
import global.namespace.fun.io.zip.zip.io.ZipFileStore;
import global.namespace.fun.io.zip.zip.io.ZipInput;
import global.namespace.fun.io.zip.zip.io.ZipSink;
import global.namespace.fun.io.zip.zip.io.ZipSource;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.security.MessageDigest;

import static java.util.Objects.requireNonNull;

/**
 * Compares two archives entry by entry.
 * Archives may be ZIP, JAR, EAR or WAR files.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public abstract class ZipDiff {

    /** Returns a new builder for a ZIP diff. */
    public static Builder builder() { return new Builder(); }

    public abstract void output(File file) throws Exception;
    public abstract void output(ZipSink sink) throws Exception;

    /**
     * A builder for a ZIP diff.
     * The default message digest is SHA-1.
     */
    public static class Builder {

        private @CheckForNull ZipSource input1, input2;
        private @CheckForNull String digest;

        Builder() { }

        public Builder input1(final @Nullable File input1) {
            return input1(null == input1 ? null : new ZipFileStore(input1));
        }

        public Builder input1(final @Nullable ZipSource input1) {
            this.input1 = input1;
            return this;
        }

        public Builder input2(final @Nullable File input2) {
            return input2(null == input2 ? null : new ZipFileStore(input2));
        }

        public Builder input2(final @Nullable ZipSource input2) {
            this.input2 = input2;
            return this;
        }

        public Builder digest(final @Nullable String digest) {
            this.digest = digest;
            return this;
        }

        public ZipDiff build() { return create(input1, input2, digest); }

        private static @CreatesObligation
        ZipDiff create(final ZipSource source1, final ZipSource source2, final @Nullable String digestName) {
            requireNonNull(source1);
            requireNonNull(source2);

            return new ZipDiff() {

                @Override
                public void output(File file) throws Exception { output(new ZipFileStore(file)); }

                @Override
                public void output(final ZipSink sink) throws Exception {
                    source1.acceptReader(input1 -> {
                        source2.acceptReader(input2 -> {
                            sink.acceptWriter(delta -> {
                                new RawZipDiff() {
                                    final MessageDigest digest = MessageDigests.create(
                                            null != digestName ? digestName : "SHA-1");

                                    protected MessageDigest digest() { return digest; }

                                    protected ZipInput input1() { return input1; }

                                    protected ZipInput input2() { return input2; }
                                }.output(delta);
                            });
                        });
                    });
                }
            };
        }
    }
}
