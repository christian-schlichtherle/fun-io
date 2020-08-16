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
package global.namespace.fun.io.bios

import java.nio.file.Paths

import org.scalatest.matchers.should.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.wordspec.AnyWordSpec

import scala.jdk.CollectionConverters._

/** @author Christian Schlichtherle */
class DirectoryStoreSpec extends AnyWordSpec {

  "A directory store" when {
    "addressing the parent directory of the directory containing this class file" should {
      val clazz = classOf[DirectoryStoreSpec]
      val dir = (clazz.getPackage.getName split "\\.").last
      val store = new DirectoryStore(Paths get (clazz getResource "..").toURI)

      "inhibit access to the directory itself or any files or directories outside of the directory" in {
        val tests = Table(
          "path",
          "/",
          "/foo",
          ".",
          "./",
          "",
          "..",
          "../",
          "../foo"
        )
        forAll(tests) { path =>
          intercept[IllegalArgumentException](store acceptReader (in => in source path))
          intercept[IllegalArgumentException](store acceptWriter (out => out sink path))
        }
      }

      "list the directory and this class file" in {
        store acceptReader { in =>
          in.asScala.map(_.name) should (contain(dir + '/') and contain(s"$dir/${clazz.getSimpleName}.class"))
        }
      }
    }
  }
}
