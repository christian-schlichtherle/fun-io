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
import sbt._

object Dependencies {

  private val JAXB_Version = "2.3.0"

  val ActivationApi: ModuleID = "javax.activation" % "javax.activation-api" % "1.2.0"
  val CommonsCompress: ModuleID = "org.apache.commons" % "commons-compress" % "1.16.1"
  val JacksonDatabind: ModuleID = "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.5"
  val JaxbApi: ModuleID = "javax.xml.bind" % "jaxb-api" % JAXB_Version
  val JaxbRuntime: ModuleID = "org.glassfish.jaxb" % "jaxb-runtime" % JAXB_Version
  val MockitoCore: ModuleID = "org.mockito" % "mockito-core" % "2.17.0"
  val Scalacheck: ModuleID = "org.scalacheck" %% "scalacheck" % "1.13.5"
  val Scalatest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5"
  val Xz: ModuleID = "org.tukaani" % "xz" % "1.8"

  val ScalaVersion_2_10: String = sys.env.getOrElse("SCALA_VERSION_2_10", "2.10.7")
  val ScalaVersion_2_11: String = sys.env.getOrElse("SCALA_VERSION_2_11", "2.11.12")
  val ScalaVersion_2_12: String = sys.env.getOrElse("SCALA_VERSION_2_12", "2.12.4")
}
