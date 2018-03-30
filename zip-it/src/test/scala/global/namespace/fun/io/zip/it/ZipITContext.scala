/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import java.io.File
import java.security.MessageDigest

import edu.umd.cs.findbugs.annotations.CreatesObligation
import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.zip.diff.RawZipDiff
import global.namespace.fun.io.zip.io.{JarFileStore, MessageDigests, ZipInput}
import global.namespace.fun.io.zip.model.DeltaModel
import javax.xml.bind.JAXBContext

/** @author Christian Schlichtherle */
trait ZipITContext extends ITContext {

  def loanRawZipDiff[A](fun: RawZipDiff => A): A =
    loanTestJars { (archive1, archive2) =>
      fun(new RawZipDiff {

        def input1: ZipInput = archive1

        def input2: ZipInput = archive2

        lazy val digest: MessageDigest = MessageDigests.sha1
      })
    }

  def loanTestJars[A](fun: (ZipInput, ZipInput) => A): A = {
    new JarFileStore(testJar1).applyReader[A] { jar1: ZipInput =>
      new JarFileStore(testJar2).applyReader[A] { jar2: ZipInput =>
        fun(jar1, jar2)
      }
    }
  }

  @CreatesObligation final def testJar1: File = file("test1.jar")
  @CreatesObligation final def testJar2: File = file("test2.jar")

  private def file(resourceName: String) = new File((classOf[ZipITContext] getResource resourceName).toURI)

  lazy val jaxbContext: JAXBContext = DeltaModel.jaxbContext
}
