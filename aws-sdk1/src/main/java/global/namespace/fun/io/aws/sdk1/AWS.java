/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.fun.io.aws.sdk1;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import global.namespace.fun.io.api.*;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;
import static global.namespace.fun.io.spi.Copy.copy;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * This facade provides static factory methods for Amazon Web Services.
 *
 * @author Christian Schlichtherle
 */
public final class AWS {

    private AWS() {
    }

    /**
     * Returns an archive store using the given S3 client for the named S3 bucket.
     */
    public static ArchiveStore s3(AmazonS3 client, String bucket) {
        return s3(client, bucket, "");
    }

    /**
     * Returns an archive store using the given S3 client for the named S3 bucket and prefix.
     */
    public static ArchiveStore s3(final AmazonS3 client, final String bucket, final String prefix) {
        final String normalized = prefix.isEmpty() ? prefix : requireInternal(prefix);
        if (!normalized.equals(prefix)) {
            throw new IllegalArgumentException("prefix must be in normalized form, but is `" + prefix + "`.");
        }
        if (!(normalized.isEmpty() || normalized.endsWith("/"))) {
            throw new IllegalArgumentException("prefix must be empty or end with a `/`, but is `" + prefix + "`.");
        }
        return checked(client, bucket, normalized);
    }

    private static ArchiveStore checked(AmazonS3 client, String bucket, String prefix) {
        return new ArchiveStore() {

            @Override
            public Socket<ArchiveInput> input() {
                return () -> new ArchiveInput() {

                    Map<String, S3ObjectSummary> objects = client
                            .listObjectsV2(bucket, prefix)
                            .getObjectSummaries()
                            .stream()
                            .collect(Collectors.toMap(
                                    o -> o.getKey().substring(prefix.length()),
                                    Function.identity()
                            ));

                    @Override
                    public Iterator<ArchiveEntrySource> iterator() {
                        return requireNonNull(objects, "Already closed.")
                                .values()
                                .stream()
                                .map(this::source)
                                .iterator();
                    }

                    @Override
                    public Optional<ArchiveEntrySource> source(String name) {
                        return ofNullable(requireNonNull(objects, "Already closed.").get(requireInternal(name)))
                                .map(this::source);
                    }

                    ArchiveEntrySource source(S3ObjectSummary object) {
                        return new ArchiveEntrySource() {

                            @Override
                            public Socket<InputStream> input() {
                                return () -> client.getObject(object.getBucketName(), object.getKey()).getObjectContent();
                            }

                            @Override
                            public boolean isDirectory() {
                                return object.getKey().endsWith("/");
                            }

                            @Override
                            public String name() {
                                return object.getKey().substring(prefix.length());
                            }

                            @Override
                            public long size() {
                                return object.getSize();
                            }
                        };
                    }

                    @Override
                    public void close() {
                        objects = null;
                    }
                };
            }

            @Override
            public Socket<ArchiveOutput> output() {
                return () -> new ArchiveOutput() {

                    @Override
                    public ArchiveEntrySink sink(String name) {
                        final String prefixedName = prefix + requireInternal(name);
                        return new ArchiveEntrySink() {

                            @Override
                            public void copyFrom(ArchiveEntrySource source) throws Exception {
                                copy(source, this);
                            }

                            @Override
                            public Socket<OutputStream> output() {
                                return () -> {
                                    final File temp = File.createTempFile("tmp", null);
                                    temp.deleteOnExit();
                                    return new FileOutputStream(temp) {

                                        @Override
                                        public void close() throws IOException {
                                            super.close();
                                            if (temp.isFile()) { // idempotence!
                                                try {
                                                    client.putObject(bucket, prefixedName, temp);
                                                } finally {
                                                    temp.delete();
                                                }
                                            }
                                        }
                                    };
                                };
                            }
                        };
                    }

                    @Override
                    public void close() {
                    }
                };
            }
        };
    }
}
