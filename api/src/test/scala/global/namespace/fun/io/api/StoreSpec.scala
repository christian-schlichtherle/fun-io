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

import java.io.{InputStream, OutputStream}

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.Matchers.{inOrder => _, _}
import org.scalatest.WordSpec
import org.scalatest.mockito.MockitoSugar.mock

class StoreSpec extends WordSpec {

  "A store" when {
    val s = mock[Store]

    "mapping a transformation" should {
      when(s map any[Transformation]).thenCallRealMethod

      val t = mock[Transformation]
      val ts = s map t

      "apply the transformation" in {
        ts output () shouldBe null
        val io = inOrder(t, s)
        io verify s output ()
        (io verify t)(any[Loan[OutputStream]])
        io verifyNoMoreInteractions ()
      }

      "unapply the transformation" in {
        ts input () shouldBe null
        val io = inOrder(t, s)
        io verify s input ()
        io verify t unapply any[Loan[InputStream]]
        io verifyNoMoreInteractions ()
      }
    }
  }
}
