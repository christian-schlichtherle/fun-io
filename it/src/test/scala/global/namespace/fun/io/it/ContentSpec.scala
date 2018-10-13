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
package global.namespace.fun.io.it

import java.io.{File, IOException}
import java.nio.file.Files
import java.util.UUID

import global.namespace.fun.io.api._
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.scala.api._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

class ContentSpec extends WordSpec {

  "Every type of store" should {
    "support reading and writing its content" in {
      val storeProviders = Table[() => Store](
        "store provider",
        () => memory,
        () => file {
          val file = File.createTempFile("tmp", null)
          file delete ()
          file
        },
        () => path {
          val path = Files.createTempFile("tmp", null)
          Files delete path
          path
        },
        () => userPreferences(classOf[ContentSpec], UUID.randomUUID.toString)
      )
      val filters = Table[Filter](
        "filter",
        identity,
        deflate
      )
      forAll(storeProviders) { storeProvider =>
        forAll(filters) { filter =>
          val store = storeProvider() >> filter

          store.exists shouldBe false
          intercept[IOException](store.content)

          store content "123".getBytes
          store.exists shouldBe true
          new String(store.content) shouldBe "123"
          new String(store content 3) shouldBe "123"
          intercept[ContentTooLargeException](store content 2)

          store delete()
          store.exists shouldBe false
          intercept[IOException](store.content)

          store.content("123".getBytes, 1, 2)
          new String(store.content) shouldBe "23"
          new String(store content 2) shouldBe "23"
          intercept[ContentTooLargeException](store content 1)

          store delete()
          store.exists shouldBe false
          intercept[IOException](store.content)

          store.content("123".getBytes, 2, 1)
          new String(store.content) shouldBe "3"
          new String(store content 1) shouldBe "3"
          intercept[ContentTooLargeException](store content 0)

          store delete()
          store.exists shouldBe false
          intercept[IOException](store.content)

          store.content("123".getBytes, 3, 0)
          new String(store.content) shouldBe ""
          new String(store content 0) shouldBe ""
          intercept[IllegalArgumentException](store content -1)

          store delete()
          store.exists shouldBe false
          intercept[IOException](store.content)
        }
      }
    }
  }
}
