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

import java.io._
import java.lang.reflect.Type

import global.namespace.fun.io.api._
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.bios.{BIOS, BufferedInvertibleTransformation}
import global.namespace.fun.io.commons.compress.CommonsCompress
import global.namespace.fun.io.commons.compress.CommonsCompress._
import global.namespace.fun.io.it.PBE.pbe
import global.namespace.fun.io.it.TransformationCompositionSpec._
import global.namespace.fun.io.scala.api._
import global.namespace.fun.io.xz.XZ
import org.scalatest.Matchers.{a => _, _}
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

import scala.io.Source

class TransformationCompositionSpec extends WordSpec {

  "A sequence of transformations" when {
    "composed in different, yet equivalent ways" should {

      "produce identical output" in {
        val table = Table[Transformation](
          "transformation",

          base64 - base64,

          blockLZ4 - blockLZ4,

          bzip2 - bzip2,

          buffer,

          BIOS.deflate - BIOS.deflate,
          BIOS.deflate - CommonsCompress.deflate,
          CommonsCompress.deflate - BIOS.deflate,
          CommonsCompress.deflate - CommonsCompress.deflate,

          BIOS.deflate + inflate,
          CommonsCompress.deflate + inflate,

          framedLZ4 - framedLZ4,

          framedSnappy - framedSnappy,

          pbe - pbe,

          BIOS.gzip - BIOS.gzip,
          BIOS.gzip - CommonsCompress.gzip,
          CommonsCompress.gzip - BIOS.gzip,
          CommonsCompress.gzip - CommonsCompress.gzip,

          -inflate + inflate,

          lzma - lzma,

          XZ.lzma2 - XZ.lzma2,
          XZ.lzma2 - CommonsCompress.lzma2,
          CommonsCompress.lzma2 - XZ.lzma2,
          CommonsCompress.lzma2 - CommonsCompress.lzma2,

          rot1 - rot1,
          -rot1 + rot1,

          rot13 + rot13,
          rot13 - rot13,
          -rot13 + rot13,
          -rot13 - rot13
        )
        forAll(table) { transformation =>
          transformation should not be identity
          string << transformation connect memory clone "Hello world!" shouldBe "Hello world!"
        }
      }

      "be associative" in {
        val table = Table[ConnectedCodec](
          "connectedCodec",

          string << b << a << memory,
          ((string << b) << a) << memory,
          string << (b << (a << memory)),
          string << (b << a) << memory,

          memory >> a >> b >> string,
          ((memory >> a) >> b) >> string,
          memory >> (a >> (b >> string)),
          memory >> (a >> b) >> string
        )
        forAll(table) { connectedCodec =>
          connectedCodec clone "c" shouldBe "abc"
        }
      }
    }
  }
}

private object TransformationCompositionSpec {

  private object string extends Codec {

    def encoder(l: Socket[OutputStream]): Encoder = new Encoder {
      def encode(obj: AnyRef): Unit = l.accept((_: OutputStream).write(obj.toString.getBytes))
    }

    def decoder(l: Socket[InputStream]): Decoder = new Decoder {
      def decode[T](expected: Type): T = l(Source.fromInputStream(_: InputStream).mkString.asInstanceOf[T])
    }
  }

  // MUST be `def` or `rot1 - rot1` may get optimized to `identity`!
  private def rot1 = rot(1)

  private val a: Transformation = new MessageTransformation("a")

  private val b: Transformation = new MessageTransformation("b")

  private[this] class MessageTransformation(message: String) extends BufferedInvertibleTransformation {

    def apply(oss: Socket[OutputStream]): Socket[OutputStream] = {
      oss.map((out: OutputStream) => { out write message.getBytes; out })
    }

    def unapply(iss: Socket[InputStream]): Socket[InputStream] = iss
  }
}
