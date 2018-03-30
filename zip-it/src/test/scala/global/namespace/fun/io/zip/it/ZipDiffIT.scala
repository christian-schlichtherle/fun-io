/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import org.scalatest.Matchers._
import org.scalatest.WordSpec

/** @author Christian Schlichtherle */
class ZipDiffIT extends WordSpec with ZipITContext {

  "A JAR diff" when {
    "computing  the test JAR files" should {
      "partition the entry names and digests correctly" in {
        val model = loanRawZipDiff(_ model ())
        import model._

        import collection.JavaConverters._
        removedEntries.asScala map (_.name) should equal (List("entryOnlyInFile1"))
        addedEntries.asScala map (_.name) should equal (List("entryOnlyInFile2"))
        unchangedEntries.asScala map (_.name) should equal (List("META-INF/MANIFEST.MF", "differentEntryTime", "equalEntry"))
        changedEntries.asScala map (_.name) should equal (List("differentEntrySize"))
      }
    }
  }
}
