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

        lazy val digest: MessageDigest = MessageDigests.sha1

        def input1: ZipInput = archive1

        def input2: ZipInput = archive2
      })
    }

  def loanTestJars[A](fun: (ZipInput, ZipInput) => A): A = {
    testJarStore1.applyReader[A] { jar1: ZipInput =>
      testJarStore2.applyReader[A] { jar2: ZipInput =>
        fun(jar1, jar2)
      }
    }
  }

  final lazy val testJarStore1: JarStore = new JarStore(testJarFile1)

  final def testJarFile1: File = file("test1.jar")

  final lazy val testJarStore2: JarStore = new JarStore(testJarFile2)

  final def testJarFile2: File = file("test2.jar")

  private def file(resourceName: String) = new File((classOf[ZipITContext] getResource resourceName).toURI)

  lazy val jaxbContext: JAXBContext = DeltaModel.jaxbContext
}
