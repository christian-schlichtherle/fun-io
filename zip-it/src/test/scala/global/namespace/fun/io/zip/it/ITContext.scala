/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import java.nio.charset.Charset
import java.util.logging._

import global.namespace.fun.io.api.function.XConsumer
import global.namespace.fun.io.api.{Codec, Store}
import global.namespace.fun.io.bios.BIOS.memoryStore
import global.namespace.fun.io.jaxb.JAXB
import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.zip.it.ITContext._
import javax.xml.bind.{JAXBContext, Marshaller, Unmarshaller}
import org.scalatest.Matchers._

/** @author Christian Schlichtherle */
trait ITContext {

  final def assertRoundTripXmlSerializable(original: AnyRef) {
    val store = memoryStore
    val clone = jaxbCodec connect store clone original
    logger.log(Level.FINE, "\n{0}", utf8String(store))
    clone should equal (original)
    clone should not be theSameInstanceAs (original)
  }

  final lazy val jaxbCodec: Codec = JAXB.xmlCodec(jaxbContext, marshallerModifier, unmarshallerModifier)

  def jaxbContext: JAXBContext
}

/** @author Christian Schlichtherle */
private object ITContext {

  val logger: Logger = Logger.getLogger(getClass.getName)

  def utf8String(store: Store): String = new String(store.content, utf8)

  private val utf8: Charset = Charset forName "UTF-8"

  val marshallerModifier: XConsumer[Marshaller] = {
    (m: Marshaller) => m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
  }

  val unmarshallerModifier: XConsumer[Unmarshaller] = (_: Unmarshaller) => ()
}
