/*
 * Copyright (C) 2013 Schlichtherle IT Services & Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip

import java.nio.charset.Charset
import java.util.logging._

import global.namespace.fun.io.zip.TestContext._
import global.namespace.fun.io.zip.codec._
import global.namespace.fun.io.zip.io.MemoryStore
import javax.xml.bind.JAXBContext
import org.scalatest.Matchers._

/** @author Christian Schlichtherle */
object TestContext {

  val utf8 = Charset.forName("UTF-8")
}

/** @author Christian Schlichtherle */
trait TestContext {

  final lazy val logger = Logger.getLogger(getClass.getName)

  final def utf8String(store: MemoryStore) = new String(store.data, utf8)

  final def memoryStore = new MemoryStore

  final def jaxbCodec: JaxbCodec = new TestJaxbCodec(jaxbContext)

  lazy val jaxbContext: JAXBContext = throw new UnsupportedOperationException

  final def assertRoundTripXmlSerializable(original: AnyRef) {
    val store = memoryStore
    jaxbCodec encode (store, original)
    logger log (Level.FINE, "\n{0}", utf8String(store))
    val clone: AnyRef = jaxbCodec decode (store, original.getClass)
    clone should equal (original)
    clone should not be theSameInstanceAs (original)
  }
}
