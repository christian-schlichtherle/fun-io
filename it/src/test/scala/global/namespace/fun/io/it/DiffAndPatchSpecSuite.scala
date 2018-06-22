/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.it

import java.security.MessageDigest

import global.namespace.fun.io.api.{ArchiveInput, ArchiveStore}
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.delta.Delta.{diff, patch}
import global.namespace.fun.io.it.DiffAndPatchSpecSuite._
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.scalatest.Matchers._
import org.scalatest.WordSpec

import scala.collection.JavaConverters._

/** @author Christian Schlichtherle */
abstract class DiffAndPatchSpecSuite[E] extends WordSpec with ArchiveSpecContext[E] {

  "Diffing two archive stores and patching the first with the delta" should {
    "produce a clone of the second archive store" in {
      withTempArchiveStore { first: ArchiveStore[E] =>
        withTempArchiveStore { second: ArchiveStore[E] =>
          withTempJAR { delta: ArchiveStore[ZipArchiveEntry] =>
            withTempArchiveStore { clone: ArchiveStore[E] =>
              copy(Test1Jar, first)
              copy(Test2Jar, second)

              diff base first update second digest sha1 to delta
              patch base first delta delta to clone

              val secondEntries: Set[String] = second applyReader {
                (_: ArchiveInput[_]).asScala.filterNot(_.isDirectory).map(_.name).toSet
              }

              val model = (diff base second update clone digest md5).toModel
              model.changedEntries shouldBe empty
              model.addedEntries shouldBe empty
              model.removedEntries shouldBe empty
              model.unchangedEntries.asScala.map(_.name).toSet shouldBe secondEntries
            }
          }
        }
      }
    }
  }
}

private object DiffAndPatchSpecSuite {

  def sha1: MessageDigest = MessageDigest getInstance "SHA-1"

  def md5: MessageDigest = MessageDigest getInstance "MD5"
}
