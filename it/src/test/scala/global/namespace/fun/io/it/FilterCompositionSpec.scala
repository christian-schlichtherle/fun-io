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
import global.namespace.fun.io.bios.BIOS
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.commons.compress.CommonsCompress
import global.namespace.fun.io.it.FilterCompositionSpec._
import global.namespace.fun.io.scala.api._
import org.scalatest.Matchers.{a => _, _}
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

import scala.io.Source

class FilterCompositionSpec extends WordSpec {

  "A sequence of filters" when {
    "composed in different, yet equivalent ways" should {

      "produce identical output" in {
        val table = Table[Filter](
          "filter",
          buffer,
          BIOS.deflate + inflate,
          CommonsCompress.deflate + inflate
        )
        forAll(table) { filter =>
          filter should not be identity
          string << filter connect memory clone "Hello world!" shouldBe "Hello world!"
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

private object FilterCompositionSpec {

  private object string extends Codec {

    def encoder(l: Socket[OutputStream]): Encoder = new Encoder {
      def encode(obj: AnyRef): Unit = l.accept((_: OutputStream).write(obj.toString.getBytes))
    }

    def decoder(l: Socket[InputStream]): Decoder = new Decoder {
      def decode[T](expected: Type): T = l(Source.fromInputStream(_: InputStream).mkString.asInstanceOf[T])
    }
  }

  private def rot13: Filter = new ROTFilter

  private val a: Filter = new MessageFilter("a")

  private val b: Filter = new MessageFilter("b")

  private[this] class MessageFilter(message: String) extends Filter {

    def apply(oss: Socket[OutputStream]): Socket[OutputStream] = {
      oss.map((out: OutputStream) => { out write message.getBytes; out })
    }

    def unapply(iss: Socket[InputStream]): Socket[InputStream] = iss
  }
}
