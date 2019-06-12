/*
 * Copyright © 2017 - 2019 Schlichtherle IT Services
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

import java.security.MessageDigest

import global.namespace.fun.io.api.{ArchiveInputStream, ArchiveStore}
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.delta.Delta.{diff, patch}
import global.namespace.fun.io.it.DiffAndPatchSpecSuite._
import org.scalatest.Matchers._
import org.scalatest.WordSpec

import scala.jdk.CollectionConverters._

/** @author Christian Schlichtherle */
abstract class DiffAndPatchSpecSuite extends WordSpec with ArchiveSpecContext {

  "Diffing two archive stores and patching the first with the delta" should {
    "produce a clone of the second archive store" in {
      withTempArchiveStore { first: ArchiveStore =>
        withTempArchiveStore { second: ArchiveStore =>
          withTempJAR { delta: ArchiveStore =>
            withTempArchiveStore { clone: ArchiveStore =>
              copy(Test1Jar, first)
              copy(Test2Jar, second)

              diff base first update second digest sha1 to delta
              patch base first delta delta to clone

              val secondEntries: Set[String] = second applyReader {
                (_: ArchiveInputStream).asScala.filterNot(_.directory).map(_.name).toSet
              }

              val model = (diff base second update clone digest md5).toModel
              model.changedEntries shouldBe empty
              model.addedEntries shouldBe empty
              model.removedEntries shouldBe empty
              model.unchangedEntries.asScala.map(_.name).toSet shouldBe secondEntries
            }
          }
        }
      }
    }
  }
}

private object DiffAndPatchSpecSuite {

  def sha1: MessageDigest = MessageDigest getInstance "SHA-1"

  def md5: MessageDigest = MessageDigest getInstance "MD5"
}
