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
package global.namespace.fun.io.api

import java.io._

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.Matchers.{inOrder => _, _}
import org.scalatest.WordSpec
import org.scalatest.mockito.MockitoSugar.mock

class CodecSpec extends WordSpec {

  "A codec" when {
    val c = mock[Codec]

    "mapping a transformation" should {
      when(c map any[Transformation]).thenCallRealMethod

      val t = mock[Transformation]
      val tc = c map t

      "apply the transformation" in {
        val l = mock[Sink]
        tc encoder l shouldBe null
        val io = inOrder(t, c)
        io verify t apply l
        io verify c encoder any[Sink]
        io verifyNoMoreInteractions ()
      }

      "unapply the transformation" in {
        val l = mock[Source]
        tc decoder l shouldBe null
        val io = inOrder(t, c)
        io verify t unapply l
        io verify c decoder any[Source]
        io verifyNoMoreInteractions ()
      }
    }
  }
}
