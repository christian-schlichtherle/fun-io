/*
 * Copyright © 2017 - 2020 Schlichtherle IT Services
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

  private val JAXB_Version = "2.3.3"

  val AwsJavaSdkS3: ModuleID = "com.amazonaws" % "aws-java-sdk-s3" % "1.11.901"
  val CommonsCompress: ModuleID = "org.apache.commons" % "commons-compress" % "1.20"
  val Config: ModuleID = "com.typesafe" % "config" % "1.4.1"
  val JacksonDatabind: ModuleID = "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.3"
  val JaxbApi: ModuleID = "jakarta.xml.bind" % "jakarta.xml.bind-api" % JAXB_Version
  val JaxbRuntime: ModuleID = "org.glassfish.jaxb" % "jaxb-runtime" % JAXB_Version
  val MockitoCore: ModuleID = "org.mockito" % "mockito-core" % "3.6.0"
  val S3: ModuleID = "software.amazon.awssdk" % "s3" % "2.15.28"
  val Scalatest: ModuleID = "org.scalatest" %% "scalatest" % "3.2.3"
  val ScalatestplusMockito = "org.scalatestplus" %% "mockito-3-4" % "3.2.3.0"
  val Slf4jSimple: ModuleID = "org.slf4j" % "slf4j-simple" % "1.7.30"
  val Xz: ModuleID = "org.tukaani" % "xz" % "1.8"
  val ZstdJni: ModuleID = "com.github.luben" % "zstd-jni" % "1.4.5-12"

  val ScalaVersion_2_10: String = sys.env.getOrElse("SCALA_VERSION_2_10", "2.10.7")
  val ScalaVersion_2_11: String = sys.env.getOrElse("SCALA_VERSION_2_11", "2.11.12")
  val ScalaVersion_2_12: String = sys.env.getOrElse("SCALA_VERSION_2_12", "2.12.12")
  val ScalaVersion_2_13: String = sys.env.getOrElse("SCALA_VERSION_2_13", "2.13.3")
}
