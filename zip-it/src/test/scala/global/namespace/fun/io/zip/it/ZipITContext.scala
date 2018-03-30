/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import java.io.File
import java.security.MessageDigest

import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.zip.diff.ZipDiffEngine
import global.namespace.fun.io.zip.io.{JarStore, MessageDigests, ZipInput}
import global.namespace.fun.io.zip.model.DeltaModel
import javax.xml.bind.JAXBContext

/** @author Christian Schlichtherle */
trait ZipITContext extends ITContext {

  def loanZipDiffEngine[A](fun: ZipDiffEngine => A): A =
    loanTestJars { (archive1, archive2) =>
      fun(new ZipDiffEngine {

        def input1: ZipInput = archive1

        def input2: ZipInput = archive2

        lazy val digest: MessageDigest = MessageDigests.sha1
      })
    }

  def loanTestJars[A](fun: (ZipInput, ZipInput) => A): A = {
    new JarStore(testJar1).applyReader[A] { jar1: ZipInput =>
      new JarStore(testJar2).applyReader[A] { jar2: ZipInput =>
        fun(jar1, jar2)
      }
    }
  }

  final def testJar1: File = file("test1.jar")
  final def testJar2: File = file("test2.jar")

  private def file(resourceName: String) = new File((classOf[ZipITContext] getResource resourceName).toURI)

  lazy val jaxbContext: JAXBContext = DeltaModel.jaxbContext
}
