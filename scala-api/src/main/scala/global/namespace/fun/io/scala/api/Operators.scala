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
package global.namespace.fun.io.scala.api

import global.namespace.fun.io.api._

/** Provides operators for an enhanced user experience with the Fun I/O API in Scala.
  *
  * @author Christian Schlichtherle */
private[api] trait Operators {

  implicit class WithFilter(f1: Filter) {

    def +(t2: Filter): Filter = f1 compose t2

    def <<(f2: Filter): Filter = f1 compose f2
    def <<(s: Store): Store = f1 store s

    def >>(f2: Filter): Filter = f1 andThen f2
    def >>(c: Codec): Codec = f1 codec c
  }

  implicit class WithStore(s: Store) {

    def >>(f: Filter): Store = s map f
    def >>(c: Codec): ConnectedCodec = s connect c
  }

  implicit class WithCodec(c: Codec) {

    def <<(f: Filter): Codec = c map f
    def <<(s: Store): ConnectedCodec = c connect s
  }
}
