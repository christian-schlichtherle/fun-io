/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import java.io._

import global.namespace.fun.io.zip.io.MessageDigests
import global.namespace.fun.io.zip.zip.diff.{RawZipDiff, ZipDiff}
import global.namespace.fun.io.zip.zip.io.{ZipInput, ZipInputTask, ZipSources}
import global.namespace.fun.io.zip.zip.patch.ZipPatch
import org.scalatest.Matchers._
import org.scalatest.WordSpec

import scala.collection.JavaConverters._

/** @author Christian Schlichtherle */
class ZipPatchIT extends WordSpec with ZipITContext {

  def tempFile() = File.createTempFile("tmp", null)

  def fileEntryNames(zip: ZipInput) = List.empty[String] ++
    zip.iterator.asScala.filter(!_.isDirectory).map(_.getName)

  "A ZIP patch" when {
    "generating and applying the ZIP patch file to the first test JAR file" should {
      "reconstitute the second test JAR file" in {

        val deltaZip = tempFile()
        try {
          val patched = tempFile()
          try {
            ZipDiff.builder.input1(testJar1).input2(testJar2).build.output(deltaZip)
            ZipPatch.builder.input(testJar1).delta(deltaZip).build.output(patched)

            class ComputeReferenceAndDiffTask extends ZipInputTask[Unit] {
              override def execute(archive1: ZipInput) {
                val unchangedReference = fileEntryNames(archive1)

                class DiffTask extends ZipInputTask[Unit] {
                  override def execute(archive2: ZipInput) {
                    val model = new RawZipDiff {
                      val _digest = MessageDigests.sha1

                      override def digest = _digest
                      override def input1 = archive1
                      override def input2 = archive2
                    } model ()
                    model.addedEntries.isEmpty should be (true)
                    model.removedEntries.isEmpty should be (true)
                    model.unchangedEntries.asScala map (_.name) should
                      equal (unchangedReference)
                    model.changedEntries.isEmpty should be (true)
                  }
                }

                ZipSources execute new DiffTask on patched
              }
            }

            ZipSources execute new ComputeReferenceAndDiffTask on testJar2
          } finally {
            patched delete ()
          }
        } finally {
          deltaZip delete ()
        }
      }
    }
  }
}
