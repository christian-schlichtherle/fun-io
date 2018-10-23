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

import global.namespace.fun.io.api.{ArchiveInputStream, ArchiveStore}
import global.namespace.fun.io.delta.Delta.diff
import global.namespace.fun.io.spi.Copy.copy
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

import scala.collection.JavaConverters._

/** @author Christian Schlichtherle */
abstract class ArchiveSpecSuite extends WordSpec with ArchiveSpecContext {

  "An archive store" should {
    "support copying its entries" in {
      forAll(Table("JAR", Test1Jar, Test2Jar)) { inputJar: ArchiveStore =>
        withTempArchiveStore { tempArchive: ArchiveStore =>
          withTempJAR { outputJar: ArchiveStore =>
            copy(inputJar, tempArchive)
            copy(tempArchive, outputJar)

            val inputEntries: Set[String] = inputJar applyReader {
              (_: ArchiveInputStream).asScala.filterNot(_.directory).map(_.name).toSet
            }

            val model = (diff base inputJar update outputJar).toModel
            model.changedEntries shouldBe empty
            model.addedEntries shouldBe empty
            model.removedEntries shouldBe empty
            model.unchangedEntries.asScala.map(_.name).toSet shouldBe inputEntries
          }
        }
      }
    }
  }
}
