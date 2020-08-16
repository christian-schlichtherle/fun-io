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

import java.io._
import java.lang.reflect.Type

import global.namespace.fun.io.api._
import global.namespace.fun.io.bios.BIOS
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.commons.compress.CommonsCompress
import global.namespace.fun.io.it.FilterCompositionSpec._
import global.namespace.fun.io.scala.api._
import org.scalatest.matchers.should.Matchers.{a => _, _}
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

/**
 * @author Christian Schlichtherle
 */
class FilterCompositionSpec extends AnyWordSpec {

  "A composition of filters" should {
    "produce identical output" in {
      val table = Table[Filter](
        "filter",
        buffer,
        BIOS.deflate + inflate,
        CommonsCompress.deflate + inflate
      )
      forAll(table) { filter =>
        filter should not be identity
        val store = memory
        codec << filter << store clone "c" shouldBe "c"
        new String(store.content) shouldBe "c"
      }
    }

    "be associative" in {
      val store = memory
      val table = Table[ConnectedCodec](
        "connectedCodec",

        codec << b << a << store,
        ((codec << b) << a) << store,
        codec << (b << (a << store)),
        codec << (b << a) << store,

        store >> a >> b >> codec,
        ((store >> a) >> b) >> codec,
        store >> (a >> (b >> codec)),
        store >> (a >> b) >> codec
      )
      forAll(table) { connectedCodec =>
        connectedCodec clone "c" shouldBe "c"
        new String(store.content) shouldBe "abc"
      }
    }
  }
}

private object FilterCompositionSpec {

  private val codec: Codec = new Codec {

    def encoder(output: Socket[OutputStream]): Encoder = new Encoder {
      def encode(obj: AnyRef): Unit = output.accept(_.write(obj.toString.getBytes))
    }

    def decoder(input: Socket[InputStream]): Decoder = new Decoder {
      def decode[T](expected: Type): T = input(Source.fromInputStream(_).mkString.asInstanceOf[T])
    }
  }

  private val a: Filter = new PrefixFilter('a')

  private val b: Filter = new PrefixFilter('b')

  private[this] class PrefixFilter(prefix: Byte) extends Filter {

    override def output(oss: Socket[OutputStream]): Socket[OutputStream] = {
      oss.map(out => {
        out write prefix
        out
      })
    }

    override def input(iss: Socket[InputStream]): Socket[InputStream] = {
      iss.map(in => {
        val c = in.read()
        if (c != prefix) {
          throw new IOException(s"Input '$c' doesn't match prefix '$prefix'.")
        }
        in
      })
    }
  }
}
