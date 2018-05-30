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
package global.namespace.fun.io.api;

import java.io.IOException;
import java.util.Locale;

/**
 * Indicates that the content is too large.
 *
 * @author Christian Schlichtherle
 */
public class ContentTooLargeException extends IOException {

    private static final long serialVersionUID = 0L;

    public ContentTooLargeException() { }

    public ContentTooLargeException(long length, int max) {
        this(String.format(Locale.ENGLISH, "Content size %,d exceeds %,d bytes.", length, max));
    }

    public ContentTooLargeException(String message) { super(message); }

    public ContentTooLargeException(String message, Throwable cause) { super(message, cause); }

    public ContentTooLargeException(Throwable cause) { super(cause); }
}
