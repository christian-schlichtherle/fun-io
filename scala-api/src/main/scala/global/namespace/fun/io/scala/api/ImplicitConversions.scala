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

import java.io.{InputStream, OutputStream}

import global.namespace.fun.io.api.function.{XConsumer, XFunction, XSupplier}
import global.namespace.fun.io.api.{Sink, Socket, Source}

import _root_.scala.language.implicitConversions

/** Implicit conversions for use with Scala 2.10 and 2.11. Not required for Scala 2.12.
  *
  * @author Christian Schlichtherle
  */
private[api] trait ImplicitConversions {

  implicit def convertToXConsumer[A](consumer: A => Any): XConsumer[A] = new XConsumer[A] {

    def accept(t: A): Unit = consumer(t)
  }

  implicit def convertToXFunction[A, B](function: A => B): XFunction[A, B] = new XFunction[A, B] {

    def apply(a: A): B = function(a)
  }

  implicit def convertToXSupplier[A](a: () => A): XSupplier[A] = new XSupplier[A] {

    def get(): A = a()
  }

  implicit def convertToSocket[A <: AutoCloseable](a: () => A): Socket[A] = new Socket[A] {

    def get(): A = a()
  }

  implicit def convertToSource(in: () => Socket[_ <: InputStream]): Source = new Source {

    def input(): Socket[InputStream] = () => in().get()
  }

  implicit def convertToSink(out: () => Socket[OutputStream]): Sink = new Sink {

    def output(): Socket[OutputStream] = () => out().get()
  }
}
