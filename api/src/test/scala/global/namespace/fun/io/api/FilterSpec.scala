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
package global.namespace.fun.io.api

import java.io.{InputStream, OutputStream}

import global.namespace.fun.io.api.Filter._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.{inOrder => _, _}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock

/**
  * @author Christian Schlichtherle
  */
class FilterSpec extends AnyWordSpec {

  "The identity filter" should {

    "be a singleton" in {
      IDENTITY should be theSameInstanceAs IDENTITY
    }

    "return the given input stream socket" in {
      val input = mock[Socket[InputStream]]
      IDENTITY input input should be theSameInstanceAs input
    }

    "return the given output stream socket" in {
      val output = mock[Socket[OutputStream]]
      IDENTITY output output should be theSameInstanceAs output
    }
  }

  "A filter" when {
    val f = mock[Filter]

    "applied to a store" should {
      val s = mock[Store]

      when(f store any[Store]).thenCallRealMethod
      val fs = f store s

      "apply to the output" in {
        val oss1 = mock[Socket[OutputStream]]
        val oss2 = mock[Socket[OutputStream]]

        when(s.output()) thenReturn oss1
        when(f output oss1) thenReturn oss2

        fs.output() shouldBe oss2
      }

      "apply to the input" in {
        val iss1 = mock[Socket[InputStream]]
        val iss2 = mock[Socket[InputStream]]

        when(s.input()) thenReturn iss1
        when(f input iss1) thenReturn iss2

        fs.input() shouldBe iss2
      }
    }

    "applied to a codec" should {
      val c = mock[Codec]

      when(f codec any[Codec]).thenCallRealMethod
      val fc = f codec c

      "apply to the encoder" in {
        val oss1 = mock[Socket[OutputStream]]
        val oss2 = mock[Socket[OutputStream]]
        when(f output oss1) thenReturn oss2

        val e = mock[Encoder]
        when(c encoder oss2) thenReturn e

        fc encoder oss1 shouldBe e
      }

      "apply to the decoder" in {
        val iss1 = mock[Socket[InputStream]]
        val iss2 = mock[Socket[InputStream]]
        when(f input iss1) thenReturn iss2

        val d = mock[Decoder]
        when(c decoder iss2) thenReturn d

        fc decoder iss1 shouldBe d
      }
    }
  }
}
