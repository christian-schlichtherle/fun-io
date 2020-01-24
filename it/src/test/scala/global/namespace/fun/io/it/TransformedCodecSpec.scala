/*
 * Copyright © 2017 - 2019 Schlichtherle IT Services
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
import java.util.OptionalLong

import global.namespace.fun.io.api.{Codec, Filter, Store}
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.bios.{BIOS => B}
import global.namespace.fun.io.commons.compress.CommonsCompress._
import global.namespace.fun.io.commons.compress.{CommonsCompress => CC}
import global.namespace.fun.io.it.PBE.pbe
import global.namespace.fun.io.jackson.Jackson._
import global.namespace.fun.io.jaxb.JAXB.{xml => jaxb}
import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.xz.XZ
import global.namespace.fun.io.zstd.Zstd._
import javax.xml.bind.JAXBContext
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks._

import scala.language.existentials

class TransformedCodecSpec extends WordSpec {

  private val codecs = Table[Codec](
    "codec",

    jaxb(JAXBContext newInstance classOf[Bean]),
    json,
    serialization,
    xml
  )

  private val filters = Table[Filter](
    "filter",

    base64 + buffer,
    base64 + rot13 + buffer,

    blockLZ4 + buffer,
    blockLZ4 + base64 + buffer,
    blockLZ4 + pbe + buffer,
    blockLZ4 + pbe + base64 + buffer,

    bzip2 + buffer,
    bzip2 + base64 + buffer,
    bzip2 + pbe + buffer,
    bzip2 + pbe + base64 + buffer,

    B.deflate + buffer,
    B.deflate + base64 + buffer,
    B.deflate + pbe + buffer,
    B.deflate + pbe + base64 + buffer,

    CC.deflate + buffer,
    CC.deflate + base64 + buffer,
    CC.deflate + pbe + buffer,
    CC.deflate + pbe + base64 + buffer,

    framedLZ4 + buffer,
    framedLZ4 + base64 + buffer,
    framedLZ4 + pbe + buffer,
    framedLZ4 + pbe + base64 + buffer,

    framedSnappy + buffer,
    framedSnappy + base64 + buffer,
    framedSnappy + pbe + buffer,
    framedSnappy + pbe + base64 + buffer,

    B.gzip + buffer,
    B.gzip + base64 + buffer,
    B.gzip + pbe + buffer,
    B.gzip + pbe + base64 + buffer,

    CC.gzip + buffer,
    CC.gzip + base64 + buffer,
    CC.gzip + pbe + buffer,
    CC.gzip + pbe + base64 + buffer,

    CC.lzma + buffer,
    CC.lzma + base64 + buffer,
    CC.lzma + pbe + buffer,
    CC.lzma + pbe + base64 + buffer,

    XZ.lzma + buffer,
    XZ.lzma + base64 + buffer,
    XZ.lzma + pbe + buffer,
    XZ.lzma + pbe + base64 + buffer,

    CC.lzma2 + buffer,
    CC.lzma2 + base64 + buffer,
    CC.lzma2 + pbe + buffer,
    CC.lzma2 + pbe + base64 + buffer,

    XZ.lzma2 + buffer,
    XZ.lzma2 + base64 + buffer,
    XZ.lzma2 + pbe + buffer,
    XZ.lzma2 + pbe + base64 + buffer,

    pbe + buffer,
    pbe + base64 + buffer,

    rot13 + buffer,

    zstd + buffer,
    zstd + base64 + buffer,
    zstd + pbe + buffer,
    zstd + pbe + base64 + buffer
  )

  private def forAllTransformedCodecs(block: Codec => Unit): Unit = {
    forAll(codecs) { codec =>
      forAll(filters) { filter =>
        block(codec << filter)
      }
    }
  }

  private def assertCloneableUsing(f: Bean => Bean): Unit = {
    val bean = Bean("Hello world!")
    val clone = f(bean)
    clone shouldBe bean
    clone shouldNot be theSameInstanceAs bean
  }

  private def assertEmptyStore(store: Store): Unit = {
    store.size shouldBe OptionalLong.empty
    store.exists shouldBe false
    intercept[Exception](store.acceptReader((_: InputStream) => ()))
  }

  private def assertNonEmptyStore(store: Store): Unit = {
    store.size should not be OptionalLong.empty
    store.exists shouldBe true
    store.acceptReader((_: InputStream) => ())
  }

  private def rot13: Filter = new ROTFilter

  "All transformed codecs" should {

    "pass the lifecycle" in {
      forAllTransformedCodecs { transformedCodec =>
        val store = memory
        assertEmptyStore(store)
        assertCloneableUsing((transformedCodec << store).clone)
        assertNonEmptyStore(store)
        store.delete()
        assertEmptyStore(store)
      }
    }

    "clone an object" in {
      forAllTransformedCodecs { transformedCodec =>
        assertCloneableUsing(transformedCodec.clone(_, () => memory))
      }
    }
  }
}
