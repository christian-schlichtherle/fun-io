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
package global.namespace.fun.io.bios

import java.io.{ByteArrayInputStream, InputStream, OutputStream}

import global.namespace.fun.io.api.{Socket, Source, Store}
import global.namespace.fun.io.bios.BIOS._
import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.mockito.MockitoSugar.mock

class BIOSSpec extends WordSpec {

  "A socket returned from BIOS.stream" should {
    "never close the given stream" when {
      "given an input stream" in {
        val in = mock[InputStream]
        val source = stream(in)
        source acceptReader (_.read)
        verify(in) read ()
        verify(in, never) close ()
      }

      "given an output stream" in {
        val out = mock[OutputStream]
        val sink = stream(out)
        sink acceptWriter ((_: OutputStream) write 0)
        verify(out) write 0
        verify(out) flush ()
        verify(out, never) close ()
      }
    }
  }

  "BIOS.content" should {
    "return the content of the parameter object" when given {
      "a store" in {
        val store = mock[Store]
        when(store content Int.MaxValue) thenReturn "Hello world!".getBytes
        new String(content(store)) shouldBe "Hello world!"
      }

      "a source" in {
        val socket: Socket[InputStream] = () => new ByteArrayInputStream("Hello world!".getBytes)
        val source = mock[Source]
        when(source.input) thenReturn socket
        new String(content(source)) shouldBe "Hello world!"
      }
    }
  }

  private def given = afterWord("given")
}
