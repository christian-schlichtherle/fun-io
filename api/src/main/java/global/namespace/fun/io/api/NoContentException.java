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

/**
 * Indicates that there is no content.
 *
 * @author Christian Schlichtherle
 */
public class NoContentException extends IOException {

    private static final long serialVersionUID = 0L;

    public NoContentException() { }

    public NoContentException(String message) { super(message); }

    public NoContentException(String message, Throwable cause) { super(message, cause); }

    public NoContentException(Throwable cause) { super(cause); }
}
