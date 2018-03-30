/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.core.it

import edu.umd.cs.findbugs.annotations.CreatesObligation
import java.io.File
import java.util.jar.JarFile
import javax.annotation.WillNotClose
import net.java.trueupdate.core.TestContext
import net.java.trueupdate.core.io._
import net.java.trueupdate.core.zip._
import net.java.trueupdate.core.zip.diff.RawZipDiff
import net.java.trueupdate.core.zip.model.DeltaModel
import net.java.trueupdate.core.zip.patch._
import net.java.trueupdate.core.zip.io.{ZipSources, ZipInputTask, ZipInput}

/**
 * @author Christian Schlichtherle
 */
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

  override lazy val jaxbContext = DeltaModel.jaxbContext
}
