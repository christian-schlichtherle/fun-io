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

import java.io._

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers.{inOrder => _, _}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock

class InternalSpec extends AnyWordSpec {

  "The filter returned by the `compose` method" should {
    val a = mock[Filter]
    val b = mock[Filter]
    val ab = Internal.compose(a, b)

    "apply its parameter filters in order" when {
      "being applied" in {
        ab output mock[Socket[OutputStream]] shouldBe null
        val io = inOrder(a, b)
        io verify b output any[Socket[OutputStream]]
        io verify a output any[Socket[OutputStream]]
        io.verifyNoMoreInteractions()
      }

      "being unapplied" in {
        ab input mock[Socket[InputStream]] shouldBe null
        val io = inOrder(a, b)
        io verify b input any[Socket[InputStream]]
        io verify a input any[Socket[InputStream]]
        io.verifyNoMoreInteractions()
      }
    }
  }
}
