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

class InternalSpec extends WordSpec {

  "The transformation returned by the `compose` method" should {
    val a = mock[Transformation]
    val b = mock[Transformation]
    val c = Internal.compose(a, b)

    "apply its parameter transformations in order" when {
      "being applied" in {
        c(mock[Loan[OutputStream]]) shouldBe null
        val io = inOrder(a, b)
        (io verify a)(any[Loan[OutputStream]])
        (io verify b)(any[Loan[OutputStream]])
        io verifyNoMoreInteractions ()
      }

      "being unapplied" in {
        c unapply mock[Loan[InputStream]] shouldBe null
        val io = inOrder(a, b)
        io verify a unapply any[Loan[InputStream]]
        io verify b unapply any[Loan[InputStream]]
        io verifyNoMoreInteractions ()
      }
    }

    "return itself again" when {
      "inverting it twice" in {
        c.inverse.inverse should be theSameInstanceAs c
      }
    }
  }
}
