/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.bios

import java.nio.file.Paths

import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

import scala.collection.JavaConverters._

/** @author Christian Schlichtherle */
class DirectoryStoreSpec extends WordSpec {

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
