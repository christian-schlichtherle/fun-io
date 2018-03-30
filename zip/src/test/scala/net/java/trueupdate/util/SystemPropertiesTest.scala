/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.util

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers._
import org.scalatest.prop.PropertyChecks._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class SystemPropertiesTest extends WordSpec {

  "Replacing system properties " should {
    "work for a list of test strings" in {
      val userHome = System.getProperty("user.home")
      val table = Table(
        ("string", "result"),
        ("${user.home}", userHome),
        ("${user.home}/.m2/repository", userHome + "/.m2/repository"),
        ("foo${user.home}bar", "foo" + userHome + "bar")
      )
      forAll(table) { (string, result) =>
        SystemProperties.resolve(string) should equal (result)
      }
    }
  }
}
