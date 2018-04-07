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
package global.namespace.fun.io.scala

import java.io.{InputStream, OutputStream}

import global.namespace.fun.io.api.Transformation.IDENTITY
import global.namespace.fun.io.api.function.{XConsumer, XFunction, XSupplier}
import global.namespace.fun.io.{api => j}

import _root_.scala.language.implicitConversions

/** @author Christian Schlichtherle */
package object api {

  type Buffer = j.Buffer
  type Codec = j.Codec
  type ConnectedCodec = j.ConnectedCodec
  type Decoder = j.Decoder
  type Encoder = j.Encoder
  type Sink = j.Sink
  type Socket[T <: AutoCloseable] = j.Socket[T]
  type Source = j.Source
  type Store = j.Store
  type Transformation = j.Transformation

  implicit class WithTransformation(t1: Transformation) {

    def unary_- : Transformation = t1.inverse

    def +(t2: Transformation): Transformation = t1 compose t2
    def -(t2: Transformation): Transformation = if (t1 == t2) IDENTITY else t1 + -t2

    def <<(t2: Transformation): Transformation = t1 compose t2
    def <<(s: Store): Store = s map t1

    def >>(t2: Transformation): Transformation = t1 andThen t2
    def >>(c: Codec): Codec = c map t1
  }

  implicit class WithStore(s: Store) {

    def >>(t: Transformation): Store = s map t
    def >>(c: Codec): ConnectedCodec = s connect c
  }

  implicit class WithCodec(c: Codec) {

    def <<(t: Transformation): Codec = c map t
    def <<(s: Store): ConnectedCodec = c connect s
  }

  implicit def xConsumer[A](consumer: A => Unit): XConsumer[A] = new XConsumer[A] {

    def accept(t: A): Unit = consumer(t)
  }

  implicit def xFunction[A, B](function: A => B): XFunction[A, B] = new XFunction[A, B] {

    def apply(a: A): B = function(a)
  }

  implicit def xSupplier[A](a: => A): XSupplier[A] = new XSupplier[A] {

    def get(): A = a
  }

  /** Creates a socket from the given by-name parameter.
    * This method is primarily required for Scala 2.10 and 2.11.
    * In Scala 2.12, the same effect could be achieved by writing the expression `(() => c): Socket[A]`.
    */
  def socket[A <: AutoCloseable](c: => A): Socket[A] = new Socket[A] {

    def get(): A = c
  }

  /** Creates a source from the given by-name parameter.
    * This method is primarily required for Scala 2.10 and 2.11.
    * In Scala 2.12, the same effect could be achieved by writing the expression `(() => () => in): Source`.
    */
  def source(in: => InputStream): Source = new Source {

    def input(): Socket[InputStream] = socket(in)
  }

  /** Creates a sink from the given by-name parameter.
    * This method is primarily required for Scala 2.10 and 2.11.
    * In Scala 2.12, the same effect could be achieved by writing the expression `(() => () => out): Sink`.
    */
  def sink(out: => OutputStream): Sink = new Sink {

    def output(): Socket[OutputStream] = socket(out)
  }
}
