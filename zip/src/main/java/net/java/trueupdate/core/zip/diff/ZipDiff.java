/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.zip.diff;

import edu.umd.cs.findbugs.annotations.CreatesObligation;
import net.java.trueupdate.core.io.MessageDigests;
import net.java.trueupdate.core.zip.io.*;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.IOException;
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

    public abstract void output(File file) throws IOException;
    public abstract void output(ZipSink sink) throws IOException;

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

        public ZipDiff build() {
            return create(input1, input2, digest);
        }

        private static @CreatesObligation
        ZipDiff create(
                final ZipSource input1,
                final ZipSource input2,
                final @Nullable String digestName) {
            requireNonNull(input1);
            requireNonNull(input2);

            return new ZipDiff() {

                @Override
                public void output(File file) throws IOException {
                    output(new ZipFileStore(file));
                }

                @Override
                public void output(final ZipSink sink) throws IOException {

                    class Input1Task implements ZipInputTask<Void, IOException> {
                        public Void execute(final ZipInput input1) throws IOException {

                            class Input2Task implements ZipInputTask<Void, IOException> {
                                public Void execute(final ZipInput input2) throws IOException {

                                    class DiffTask implements ZipOutputTask<Void, IOException> {
                                        public Void execute(final ZipOutput delta) throws IOException {
                                            new RawZipDiff() {
                                                final MessageDigest digest = MessageDigests.create(
                                                        null != digestName ? digestName : "SHA-1");

                                                protected MessageDigest digest() { return digest; }
                                                protected ZipInput input1() { return input1; }
                                                protected ZipInput input2() { return input2; }
                                            }.output(delta);
                                            return null;
                                        }
                                    } // DiffTask

                                    return ZipSinks.execute(new DiffTask()).on(sink);
                                }
                            } // Input2Task

                            return ZipSources.execute(new Input2Task()).on(input2);
                        }
                    } // Input1Task

                    ZipSources.execute(new Input1Task()).on(input1);
                }
            }; // ZipDiff
        }
    } // Builder
}
