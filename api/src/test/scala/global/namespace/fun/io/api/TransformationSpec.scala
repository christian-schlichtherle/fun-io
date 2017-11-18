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

import global.namespace.fun.io.api.Transformation._
import org.scalatest.Matchers.{inOrder => _, _}
import org.scalatest.WordSpec
import org.scalatest.mockito.MockitoSugar.mock

class TransformationSpec extends WordSpec {

  "The identity transformation" should {

    "be a singleton" in {
      IDENTITY should be theSameInstanceAs IDENTITY
    }

    "return the given output stream loan" in {
      val l = mock[Loan[OutputStream]]
      IDENTITY apply l should be theSameInstanceAs l
    }

    "return the given input stream loan" in {
      val l = mock[Loan[InputStream]]
      IDENTITY unapply l should be theSameInstanceAs l
    }

    "return itself as its inverse" in {
      IDENTITY.inverse should be theSameInstanceAs IDENTITY
    }
  }
}
