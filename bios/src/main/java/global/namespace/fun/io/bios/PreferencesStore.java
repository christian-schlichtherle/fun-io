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

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.Store;

import java.io.*;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

final class PreferencesStore implements Store {

    private final Preferences prefs;
    private final String key;

    PreferencesStore(final Preferences p, final String key) {
        this.prefs = p;
        this.key = key;
    }

    @Override
    public Socket<OutputStream> output() {
        return () -> new ByteArrayOutputStream(BUFSIZE) {
            @Override
            public void close() throws IOException { content(toByteArray()); }
        };
    }

    @Override
    public Socket<InputStream> input() { return () -> new ByteArrayInputStream(content()); }

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
    public boolean exists() throws IOException {
        try {
            return prefs.nodeExists("");
        } catch (BackingStoreException e) {
            throw new IOException(e);
        }
    }

    private byte[] content() throws IOException {
        return optContent()
                .orElseThrow(() -> new FileNotFoundException(
                        "Cannot locate the key \"" + key + "\" in the " +
                                (prefs.isUserNode() ? "user" : "system") +
                                " preferences node for the absolute path \"" +
                                prefs.absolutePath() + "\"."));
    }

    private void content(byte[] data) throws IOException {
        prefs.putByteArray(key, data);
        sync();
    }

    private Optional<byte[]> optContent() {
        return Optional.ofNullable(prefs.getByteArray(key, null));
    }

    private void sync() throws IOException {
        try { prefs.flush(); }
        catch (final BackingStoreException e) { throw new IOException(e); }
    }
}
