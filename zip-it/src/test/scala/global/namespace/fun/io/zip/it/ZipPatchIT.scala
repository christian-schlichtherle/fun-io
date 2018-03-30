/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import java.io._
import java.security.MessageDigest

import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.zip.diff.{ZipDiffEngine, ZipDiff}
import global.namespace.fun.io.zip.io.{JarFileStore, MessageDigests, ZipInput}
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
    "generating and applying the ZIP patch file to the first test JAR file" should {
      "reconstitute the second test JAR file" in {

        val deltaJar = tempFile()
        try {
          val patchedJar = tempFile()
          try {
            ZipDiff.builder.input1(testJar1).input2(testJar2).build.output(deltaJar)
            ZipPatch.builder.input(testJar1).delta(deltaJar).build.output(patchedJar)

            new JarFileStore(testJar2) acceptReader { jar2: ZipInput =>
              val unchangedReference = fileEntryNames(jar2)

              new JarFileStore(patchedJar) acceptReader { patched: ZipInput =>
                val model = new ZipDiffEngine {

                  lazy val digest: MessageDigest = MessageDigests.sha1

                  def input1: ZipInput = jar2

                  def input2: ZipInput = patched
                } model ()
                model.addedEntries.isEmpty shouldBe true
                model.removedEntries.isEmpty shouldBe true
                model.unchangedEntries.asScala map (_.name) shouldBe unchangedReference
                model.changedEntries.isEmpty shouldBe true
                ()
              }
            }
          } finally {
            patchedJar delete ()
          }
        } finally {
          deltaJar delete ()
        }
      }
    }
  }
}
