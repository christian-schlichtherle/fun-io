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
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static java.util.Arrays.copyOfRange;

final class PreferencesStore implements Store {

    private final Preferences prefs;
    private final String key;

    PreferencesStore(final Preferences p, final String key) {
        this.prefs = p;
        this.key = key;
    }

    @Override
    public Socket<InputStream> input() { return () -> new ByteArrayInputStream(content()); }

    @Override
    public Socket<OutputStream> output() {
        return () -> new ByteArrayOutputStream(BUFSIZE) {
            @Override
            public void close() throws IOException { content(toByteArray()); }
        };
    }

    @Override
    public void delete() throws IOException {
        content();
        prefs.remove(key);
        sync();
    }

    @Override
    public OptionalLong size() throws IOException {
        return optContent().map(bytes -> OptionalLong.of(bytes.length)).orElseGet(OptionalLong::empty);
    }

    @Override
    public byte[] content(final int max) throws IOException {
        if (max < 0) {
            throw new IllegalArgumentException(max + " < 0");
        }
        final Optional<byte[]> optContent = optContent();
        if (optContent.isPresent()) {
            final byte[] content = optContent.get();
            final int length = content.length;
            if (length <= max) {
                return content;
            } else {
                throw new ContentTooLargeException(length, max);
            }
        } else {
            throw new NoContentException(String.format(Locale.ENGLISH,
                    "Cannot locate the key \"%s\" in the %s preferences node for the absolute path \"%s\".",
                    key, (prefs.isUserNode() ? "user" : "system"), prefs.absolutePath()));
        }
    }

    @Override
    public void content(final byte[] b) throws IOException {
        prefs.putByteArray(key, b);
        sync();
    }

    @Override
    public void content(byte[] b, int off, int len) throws IOException {
        content(copyOfRange(b, off, off + len));
    }

    private Optional<byte[]> optContent() { return Optional.ofNullable(prefs.getByteArray(key, null)); }

    private void sync() throws IOException {
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            throw new IOException(e);
        }
    }
}
