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

import global.namespace.fun.io.api.Loan;
import global.namespace.fun.io.api.Store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.OptionalLong;

final class PathStore implements Store {

    private final Path path;

    PathStore(final Path p) { this.path = p; }

    @Override
    public Loan<OutputStream> output() { return () -> Files.newOutputStream(path); }

    @Override
    public Loan<InputStream> input() { return () -> Files.newInputStream(path); }

    @Override
    public void delete() throws IOException {
        Files.delete(path);
    }

    @Override
    public OptionalLong size() throws IOException {
        try {
            return OptionalLong.of(Files.size(path));
        } catch (NoSuchFileException ignored) {
            return OptionalLong.empty();
        }
    }

    @Override
    public boolean exists() { return Files.exists(path); }
}
