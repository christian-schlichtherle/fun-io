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

import java.io.File

import global.namespace.fun.io.api.{ArchiveInput, ArchiveStore}
import global.namespace.fun.io.commons.compress.CommonsCompress._
import global.namespace.fun.io.delta.Delta.diff
import global.namespace.fun.io.it.ArchiveSpecSuite._
import global.namespace.fun.io.spi.Copy.copy
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

import scala.collection.JavaConverters._

abstract class ArchiveSpecSuite[E] extends WordSpec {

  "An archive store" should {
    "support copying its entries" in {
      forAll(JARs) { inputJar: ArchiveStore[ZipArchiveEntry] =>
        withTempJAR { outputJar: ArchiveStore[ZipArchiveEntry] =>
          withTempArchive { tempArchive: ArchiveStore[E] =>
            copy(inputJar, tempArchive)
            copy(tempArchive, outputJar)

            val inputEntries: Set[String]  = inputJar applyReader {
              (_: ArchiveInput[_]).asScala.filterNot(_.isDirectory).map(_.name).toSet
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

  protected[this] def withTempArchive: (ArchiveStore[E] => Any) => Unit = {
    ArchiveSpecSuite.withTempArchive(archiveFactory)
  }

  protected[this] def archiveFactory: ArchiveFactory[E]
}

object ArchiveSpecSuite {

  type ArchiveFactory[E] = File => ArchiveStore[E]

  private val JARs = Table(
    "JAR",
    jar(resourceFile("test1.jar")),
    jar(resourceFile("test2.jar"))
  )

  private def resourceFile(name: String): File = {
    new File((classOf[DiffAndPatchSpec] getResource name).toURI)
  }

  private def withTempJAR: (ArchiveStore[ZipArchiveEntry] => Any) => Unit = withTempArchive(jar)

  private def withTempArchive[E](factory: ArchiveFactory[E])(test: ArchiveStore[E] => Any): Unit = {
    val file = File.createTempFile("tmp", null)
    file delete ()
    try {
      test(factory(file))
    } finally {
      deleteAll(file)
    }
  }

  private def deleteAll(file: File): Unit = {
    Option(file listFiles ()) foreach (_ foreach deleteAll)
    file delete ()
  }
}
