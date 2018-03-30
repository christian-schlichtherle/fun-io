/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.trueupdate.util.builder

import org.scalatest.Matchers._
import org.scalatest.WordSpec

import scala.collection.JavaConverters._

class ImmutableListBuilderTest extends WordSpec {

  def builder[A] = ImmutableListBuilder.create[A]

  "An immutable list builder" should {
    "support adding one null item" in {
      val list = builder.add(null: AnyRef).build
      list should equal (List(null).asJava)
    }

    "support adding two null items at once" in {
      val list = builder.add(null: AnyRef, null: AnyRef).build
      list should equal (List(null, null).asJava)
    }

    "replace one item with another item" in {
      val list = builder.add(1).set(2).build
      list should equal (List(2).asJava)
    }

    "replace one item with two items" in {
      val list = builder.add(1).set(2, 3).build
      list should equal (List(2, 3).asJava)
    }

    "replace two items with one item" in {
      val list = builder.add(1).add(2).set(3).build
      list should equal (List(3).asJava)
    }

    "replace two items with two other items" in {
      val list = builder.add(1).add(2).set(3, 4).build
      list should equal (List(3, 4).asJava)
    }

    "copy another list into its list" in {
      val list = builder.add(1).setAll(List(2, 3).asJava).build
      list should equal (List(2, 3).asJava)
    }
  }
}
