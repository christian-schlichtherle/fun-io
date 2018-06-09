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
package global.namespace.fun.io.bios

import global.namespace.fun.io.bios.ArchiveEntryNames.requireInternal
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

class ArchiveEntryNamesSpec extends WordSpec {

  "The `requireInternal` method" should {
    "return the original value for any internal archive entry name" in {
      val tests = Table(
        ("path", "normalizad"),
        ("foo", "foo"),
        ("foo/", "foo/"),
        ("bar/../foo", "foo"),
        ("bar/../foo/", "foo/")
      )
      forAll(tests)((path, normalized) => requireInternal(path) shouldBe normalized)
    }

    "throw an `IllegalArgumentException` for any external archive entry name" in {
      val tests = Table(
        "path",
        "/",
        "/foo",
        ".",
        "./",
        "",
        "..",
        "../",
        "../foo",
        "bar/..",
        "bar/../"
      )
      forAll(tests)(path => intercept[IllegalArgumentException](requireInternal(path)))
    }
  }
}
