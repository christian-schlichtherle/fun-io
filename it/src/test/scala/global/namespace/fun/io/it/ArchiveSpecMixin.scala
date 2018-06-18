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

import global.namespace.fun.io.api.ArchiveStore
import global.namespace.fun.io.commons.compress.CommonsCompress.jar
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry

/** @author Christian Schlichtherle */
trait ArchiveSpecMixin[E] {

  type ArchiveFactory[A] = File => ArchiveStore[A]

  val Test1Jar: ArchiveStore[ZipArchiveEntry] = jar(resourceFile("test1.jar"))

  val Test2Jar: ArchiveStore[ZipArchiveEntry] = jar(resourceFile("test2.jar"))

  private def resourceFile(name: String) = {
    new File((classOf[ArchiveSpecMixin[_]] getResource name).toURI)
  }

  def disabled: Boolean = false

  def withTempArchive: (ArchiveStore[E] => Any) => Unit = withTempArchiveFile(archiveFactory)

  def archiveFactory: ArchiveFactory[E]

  def withTempArchiveFile[A](factory: ArchiveFactory[A])(test: ArchiveStore[A] => Any): Unit = {
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
