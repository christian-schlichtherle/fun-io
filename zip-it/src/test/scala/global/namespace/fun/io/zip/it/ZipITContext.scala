/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.it

import java.io.File
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.logging.{Level, Logger}

import global.namespace.fun.io.api.{Codec, Store}
import global.namespace.fun.io.bios.BIOS.memoryStore
import global.namespace.fun.io.jaxb.JAXB
import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.zip.diff.ZipDiffEngine
import global.namespace.fun.io.zip.io.{JarStore, MessageDigests, ZipInput}
import global.namespace.fun.io.zip.it.ZipITContext._
import global.namespace.fun.io.zip.model.DeltaModel.jaxbContext
import javax.xml.bind.{Marshaller, Unmarshaller}
import org.scalatest.Matchers.{equal, theSameInstanceAs, _}

/** @author Christian Schlichtherle */
trait ZipITContext {

  final def loanZipDiffEngine[A](fun: ZipDiffEngine => A): A = {
    testJarStore1.applyReader[A] { jar1: ZipInput =>
      testJarStore2.applyReader[A] { jar2: ZipInput =>
        fun(new ZipDiffEngine {

          lazy val digest: MessageDigest = MessageDigests.sha1

          def input1: ZipInput = jar1

          def input2: ZipInput = jar2
        })
      }
    }
  }

  final lazy val testJarStore1: JarStore = new JarStore(file("test1.jar"))

  final lazy val testJarStore2: JarStore = new JarStore(file("test2.jar"))

  final def assertRoundTripXmlSerializable(original: AnyRef) {
    val store = memoryStore
    val clone = jaxbCodec connect store clone original
    logger.log(Level.FINE, "\n{0}", utf8String(store))
    clone should equal (original)
    clone should not be theSameInstanceAs (original)
  }
}

/** @author Christian Schlichtherle */
private object ZipITContext {

  def file(resourceName: String) = new File((classOf[ZipITContext] getResource resourceName).toURI)

  val jaxbCodec: Codec = JAXB.xmlCodec(jaxbContext, modifyMarshaller _, unmarshallerModifier _)

  private def modifyMarshaller(m: Marshaller): Unit = m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

  private def unmarshallerModifier(u: Unmarshaller): Unit = { }

  val logger: Logger = Logger.getLogger(getClass.getName)

  def utf8String(store: Store): String = new String(store.content, utf8)

  private val utf8: Charset = Charset forName "UTF-8"
}
