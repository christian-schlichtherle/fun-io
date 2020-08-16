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
package global.namespace.fun.io.scala.api

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream, OutputStream}

import global.namespace.fun.io.api._
import global.namespace.fun.io.scala.api.ApiSpec._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

/** @author Christian Schlichtherle */
class ApiSpec extends AnyWordSpec {

  "A socket" should {
    "be creatable from a by-name parameter" in {
      var count = 0
      val resource: Socket[InputStream] = () => {
        count += 1
        inputStream
      }
      def content: String = resource apply contentAsString _
      1 to Iterations foreach { _ =>
        content shouldBe Text
      }
      count shouldBe Iterations
    }
  }

  "A source" should {
    "be creatable from a by-name parameter" in {
      var count = 0
      val resource: Source = () => (() => {
        count += 1
        inputStream
      }): Socket[InputStream]
      def content: String = resource applyReader contentAsString _
      1 to Iterations foreach { _ =>
        content shouldBe Text
      }
      count shouldBe Iterations
    }
  }

  "A sink" should {
    "be creatable from a by-name parameter" in {
      var count = 0
      val resource: Sink = () => (() => {
        count += 1
        outputStream
      }): Socket[OutputStream]
      def content: String = resource applyWriter { out: OutputStream =>
        out write Text.getBytes
        out.toString
      }
      1 to Iterations foreach { _ =>
        content shouldBe Text
      }
      count shouldBe Iterations
    }
  }
}

/** @author Christian Schlichtherle */
private object ApiSpec {

  val Text = "Hello, world!"
  val Iterations = 2

  private def contentAsString(in: InputStream) = scala.io.Source.fromInputStream(in).mkString

  private def inputStream: InputStream = new ByteArrayInputStream(Text.getBytes)

  private def outputStream: OutputStream = new ByteArrayOutputStream
}
