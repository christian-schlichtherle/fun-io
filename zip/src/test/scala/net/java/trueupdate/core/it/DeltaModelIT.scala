/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.it

import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import net.java.trueupdate.core.io.MessageDigests.sha1
import net.java.trueupdate.core.zip.model.DeltaModel

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class DeltaModelIT extends WordSpec with ZipITContext {

  "A delta model" when {
    "constructed with no data" should {
      "be round-trip XML-serializable" in {
        assertRoundTripXmlSerializable(DeltaModel.builder.messageDigest(sha1).build)
      }
    }

    "computed from a ZIP diff" should {
      "be round-trip XML-serializable" in {
        assertRoundTripXmlSerializable(loanRawZipDiff(_ model ()))
      }
    }
  }
}
