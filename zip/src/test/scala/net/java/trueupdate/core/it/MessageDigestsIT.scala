/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.it

import net.java.trueupdate.core.io._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

/** @author Christian Schlichtherle */
class MessageDigestsIT extends WordSpec {

  "Computation of digests" should {
    "yield correct values" in {
      val table = Table(
        ("SHA-1 digest reference value", "resource name"),
        ("47a013e660d408619d894b20806b1d5086aab03b", "helloWorld"),
        // Note that the most significant bit is set to test signum conversion
        ("f3172822c7d08f23764aa5baee9d73ef32797b46", "twoTimesHelloWorld")
      )
      forAll(table) { (referenceValue, resourceName) =>
        import MessageDigests._
        val digest = sha1
        val source = new Source {
          def input() = classOf[MessageDigestsIT]
            .getResourceAsStream(resourceName)
        }
        updateDigestFrom(digest, source)
        valueOf(digest) should equal (referenceValue)
      }
    }
  }
}
