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
package global.namespace.fun.io.bios;

import global.namespace.fun.io.api.ContentTooLargeException;
import global.namespace.fun.io.api.NoContentException;
import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.Store;

import java.io.*;
import java.util.Optional;
import java.util.OptionalLong;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;

final class MemoryStore implements Store {

    private final int bufferSize;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<byte[]> optContent = Optional.empty();

    private int length; // only valid if optContent.isPresent() == true

    MemoryStore(final int bufferSize) {
        if (0 > (this.bufferSize = bufferSize)) {
            throw new IllegalArgumentException(bufferSize + " is a negative buffer size.");
        }
    }

    @Override
    public Socket<InputStream> input() {
        return () -> new ByteArrayInputStream(checkedContent(), 0, length);
    }

    @Override
    public Socket<OutputStream> output() {
        return () -> new ByteArrayOutputStream(bufferSize) {

            @Override
            public void close() {
                optContent = Optional.of(buf);
                length = count;
            }
        };
    }

    @Override
    public void delete() throws IOException {
        checkedContent();
        optContent = Optional.empty();
    }

    @Override
    public OptionalLong size() throws IOException {
        return optContent.isPresent() ? OptionalLong.of(length) : OptionalLong.empty();
    }

    @Override
    public boolean exists() {
        return optContent.isPresent();
    }

    private byte[] checkedContent() throws NoContentException {
        return optContent.orElseThrow(NoContentException::new);
    }

    @Override
    public byte[] content(final int max) throws IOException {
        if (max < 0) {
            throw new IllegalArgumentException(max + " < 0");
        }
        final Optional<byte[]> optContent = this.optContent;
        if (optContent.isPresent()) {
            final byte[] content = optContent.get();
            final int length = this.length;
            if (length <= max) {
                return copyOf(content, length);
            } else {
                throw new ContentTooLargeException(length, max);
            }
        } else {
            throw new NoContentException();
        }
    }

    @Override
    public void content(byte[] b, int off, int len) {
        optContent = Optional.of(copyOfRange(b, off, off + len));
        length = len;
    }
}
