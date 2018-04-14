/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.fun.io.it

import java.io.InputStream

import global.namespace.fun.io.api.{Codec, Store, Transformation}
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.it.PBE.pbe
import global.namespace.fun.io.jackson.Jackson._
import global.namespace.fun.io.jaxb.JAXB.{xml => jaxb}
import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.xz.XZ._
import javax.xml.bind.JAXBContext
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

import scala.language.existentials

class TransformedCodecSpec extends WordSpec {

  private val codecs = Table[Codec](
    "codec",

    jaxb(JAXBContext newInstance classOf[Bean]),
    json,
    serialization,
    xml
  )

  private val transformations = Table[Transformation](
    "transformation",

    base64 + buffer,
    base64 + rot13 + buffer,

    deflate + buffer,
    deflate + base64 + buffer,
    deflate + pbe + buffer,
    deflate + pbe + base64 + buffer,

    pbe + buffer,
    pbe + base64 + buffer,

    gzip + buffer,
    gzip + base64 + buffer,
    gzip + pbe + buffer,
    gzip + pbe + base64 + buffer,

    -inflate + buffer,
    -inflate + base64 + buffer,
    -inflate + pbe + buffer,
    -inflate + pbe + base64 + buffer,

    lzma2 + buffer,
    lzma2 + base64 + buffer,
    lzma2 + pbe + buffer,
    lzma2 + pbe + base64 + buffer,

    rot13 + buffer
  )

  private def forAllTransformedCodecs(block: Codec => Unit): Unit = {
    forAll(codecs) { codec =>
      forAll(transformations) { transformation =>
        block(codec << transformation)
      }
    }
  }

  private def assertCloneableUsing(f: AnyRef => AnyRef): Unit = {
    val bean = Bean("Hello world!")
    val clone = f(bean)
    clone shouldBe bean
    clone shouldNot be theSameInstanceAs bean
  }

  private def assertThatStoreIsEmpty(store: Store): Unit = {
    store.size should not be 'present
    store.exists shouldBe false
    intercept[Exception](store.acceptReader((_: InputStream) => ()))
  }

  private def assertThatStoreIsNotEmpty(store: Store): Unit = {
    store.size shouldBe 'present
    store.exists shouldBe true
    store.acceptReader((_: InputStream) => ())
  }

  "All transformed codecs" should {

    "pass the lifecycle " in {
      forAllTransformedCodecs { transformedCodec =>
        val store = memory
        assertThatStoreIsEmpty(store)
        assertCloneableUsing((transformedCodec << store).clone)
        assertThatStoreIsNotEmpty(store)
        store delete ()
        assertThatStoreIsEmpty(store)
      }
    }

    "clone an object" in {
      forAllTransformedCodecs { transformedCodec =>
        assertCloneableUsing(transformedCodec.clone(_, () => memory))
      }
    }
  }
}
