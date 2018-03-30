/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import java.io._
import java.security.MessageDigest

import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.zip.diff.{ZipDiffEngine, ZipDiff}
import global.namespace.fun.io.zip.io.{JarStore, MessageDigests, ZipInput}
import global.namespace.fun.io.zip.patch.ZipPatch
import org.scalatest.Matchers._
import org.scalatest.WordSpec

import scala.collection.JavaConverters._

/** @author Christian Schlichtherle */
class ZipPatchIT extends WordSpec with ZipITContext {

  def tempFile(): File = File.createTempFile("tmp", null)

  def fileEntryNames(zip: ZipInput): List[String] = {
    List.empty[String] ++ zip.iterator.asScala.filter(!_.isDirectory).map(_.getName)
  }

  "A ZIP patch" when {
    "generating and applying the ZIP patch file to the base JAR file" should {
      "reconstitute the update JAR file" in {

        val patchJarFile = tempFile()
        try {
          val patchJarStore = new JarStore(patchJarFile)
          val updateJarFile = tempFile()
          try {
            val updateJarStore = new JarStore(updateJarFile)

            ZipDiff.builder.base(testJarStore1).update(testJarStore2).build.outputTo(patchJarStore)
            ZipPatch.builder.base(testJarStore1).patch(patchJarStore).build.outputTo(updateJarStore)

            testJarStore2 acceptReader { jar2: ZipInput =>
              val unchangedReference = fileEntryNames(jar2)

              updateJarStore acceptReader { updated: ZipInput =>
                val model = new ZipDiffEngine {

                  val digest: MessageDigest = MessageDigests.sha1

                  def base: ZipInput = jar2

                  def update: ZipInput = updated
                } model ()
                model.addedEntries shouldBe empty
                model.removedEntries shouldBe empty
                model.unchangedEntries.asScala map (_.name) shouldBe unchangedReference
                model.changedEntries shouldBe empty
                ()
              }
            }
          } finally {
            updateJarFile delete ()
          }
        } finally {
          patchJarFile delete ()
        }
      }
    }
  }
}
