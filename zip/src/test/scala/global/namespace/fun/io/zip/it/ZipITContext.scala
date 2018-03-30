/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import java.io.File

import edu.umd.cs.findbugs.annotations.CreatesObligation
import global.namespace.fun.io.zip.TestContext
import global.namespace.fun.io.zip.io.MessageDigests
import global.namespace.fun.io.zip.zip.diff.RawZipDiff
import global.namespace.fun.io.zip.zip.io.{ZipInput, ZipInputTask, ZipSources}
import global.namespace.fun.io.zip.zip.model.DeltaModel
import javax.xml.bind.JAXBContext

/** @author Christian Schlichtherle */
trait ZipITContext extends TestContext {

  def loanRawZipDiff[A](fun: RawZipDiff => A) =
    loanTestJars { (archive1, archive2) =>
      fun(new RawZipDiff {
        override def input1 = archive1
        override def input2 = archive2
        override def digest = ZipITContext.this.digest
      })
    }

  def loanTestJars[A](fun: (ZipInput, ZipInput) => A) = {
    class Fun1Task extends ZipInputTask[A, Exception]() {
      override def execute(jar1: ZipInput) = {
        class Fun2Task extends ZipInputTask[A, Exception] {
          override def execute(jar2: ZipInput) = {
            fun(jar1, jar2)
          }
        }

        ZipSources execute new Fun2Task on testJar2()
      }
    }

    ZipSources execute new Fun1Task on testJar1()
  }

  @CreatesObligation final def testJar1() = file("test1.jar")
  @CreatesObligation final def testJar2() = file("test2.jar")

  private def file(resourceName: String) =
    new File((classOf[ZipITContext] getResource resourceName).toURI)

  def digest = MessageDigests.sha1

  lazy val jaxbContext: JAXBContext = DeltaModel.jaxbContext
}
