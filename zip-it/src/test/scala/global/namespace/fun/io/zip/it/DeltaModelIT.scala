/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import global.namespace.fun.io.zip.io.MessageDigests.sha1
import global.namespace.fun.io.zip.model.DeltaModel
import org.scalatest.WordSpec

/** @author Christian Schlichtherle */
class DeltaModelIT extends WordSpec with ZipITContext {

  "A delta model" when {
    "constructed with no data" should {
      "be round-trip XML-serializable" in {
        assertRoundTripXmlSerializable(DeltaModel.builder.messageDigest(sha1).build)
      }
    }

    "computed from a ZIP diff" should {
      "be round-trip XML-serializable" in {
        assertRoundTripXmlSerializable(loanZipDiffEngine(_ model ()))
      }
    }
  }
}
