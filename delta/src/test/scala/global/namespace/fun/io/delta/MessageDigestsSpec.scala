/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.delta

import global.namespace.fun.io.delta.MessageDigests._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

/** @author Christian Schlichtherle */
class MessageDigestsSpec extends WordSpec {

  "Computation of digests" should {
    "yield correct values" in {
      val Tests = Table(
        ("SHA-1 digest reference value", "resource name"),
        ("47a013e660d408619d894b20806b1d5086aab03b", "helloWorld"),
        // Note that the most significant bit is set to test signum conversion
        ("f3172822c7d08f23764aa5baee9d73ef32797b46", "twoTimesHelloWorld")
      )
      forAll(Tests) { (referenceValue, resourceName) =>
        WithMessageDigest
          .of(sha1)
          .digestValueOf(() => () => classOf[MessageDigestsSpec].getResourceAsStream(resourceName)) shouldBe referenceValue
      }
    }
  }
}
