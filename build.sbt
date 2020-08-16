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
import BuildSettings._
import Dependencies._

lazy val root: Project = project
  .in(file("."))
  .aggregate(api, awsSdk1, awsSdk2, bios, commonsCompress, delta, it, jackson, jaxb, scalaApi, spi, xz, zstd)
  .settings(aggregateSettings, releaseSettings)
  .settings(name := "Fun I/O")

lazy val api: Project = project
  .in(file("api"))
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      MockitoCore % Test,
      Scalatest % Test,
      ScalatestplusMockito % Test,
    ),
    name := "Fun I/O API",
    normalizedName := "fun-io-api",
  )

lazy val awsSdk1: Project = project
  .in(file("aws-sdk1"))
  .dependsOn(spi)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      MockitoCore % Test,
      AwsJavaSdkS3,
      Scalatest % Test,
      ScalatestplusMockito % Test,
    ),
    name := "Fun I/O AWS SDK1",
    normalizedName := "fun-io-aws-sdk1",
  )

lazy val awsSdk2: Project = project
  .in(file("aws-sdk2"))
  .dependsOn(spi)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      MockitoCore % Test,
      S3,
      Scalatest % Test,
      ScalatestplusMockito % Test,
    ),
    name := "Fun I/O AWS SDK2",
    normalizedName := "fun-io-aws-sdk2",
  )

lazy val bios: Project = project
  .in(file("bios"))
  .dependsOn(spi)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      MockitoCore % Test,
      Scalatest % Test,
      ScalatestplusMockito % Test,
    ),
    name := "Fun I/O BIOS",
    normalizedName := "fun-io-bios",
  )

lazy val commonsCompress: Project = project
  .in(file("commons-compress"))
  .dependsOn(spi)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      CommonsCompress,
      Xz % Optional,
    ),
    name := "Fun I/O Commons Compress",
    normalizedName := "fun-io-commons-compress",
  )

lazy val delta: Project = project
  .in(file("delta"))
  .dependsOn(bios % Test, jackson)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      Scalatest % Test,
    ),
    name := "Fun I/O Delta",
    normalizedName := "fun-io-delta",
  )

lazy val it: Project = project
  .in(file("it"))
  .dependsOn(awsSdk1, awsSdk2, bios, commonsCompress, delta, jackson, jaxb, scalaApi, spi, xz, zstd)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      Config,
      JaxbRuntime,
      MockitoCore,
      Scalatest,
      Slf4jSimple % Runtime,
    ),
    name := "Fun I/O IT",
    publish / skip := true,
  )

lazy val jackson: Project = project
  .in(file("jackson"))
  .dependsOn(api)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      JacksonDatabind,
    ),
    name := "Fun I/O Jackson",
    normalizedName := "fun-io-jackson",
  )

lazy val jaxb: Project = project
  .in(file("jaxb"))
  .dependsOn(api)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      JaxbApi % Provided,
    ),
    name := "Fun I/O JAXB",
    normalizedName := "fun-io-jaxb",
  )

lazy val scalaApi: Project = project
  .in(file("scala-api"))
  .dependsOn(api)
  .settings(scalaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      Scalatest % Test,
    ),
    name := "Fun I/O Scala API",
    normalizedName := "fun-io-scala-api",
  )

lazy val spi: Project = project
  .in(file("spi"))
  .dependsOn(api)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      Scalatest % Test,
      ScalatestplusMockito % Test,
    ),
    name := "Fun I/O SPI",
    normalizedName := "fun-io-spi",
  )

lazy val xz: Project = project
  .in(file("xz"))
  .dependsOn(api)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      Xz,
    ),
    name := "Fun I/O XZ",
    normalizedName := "fun-io-xz",
  )

lazy val zstd: Project = project
  .in(file("zstd"))
  .dependsOn(api)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      ZstdJni,
    ),
    name := "Fun I/O Zstd",
    normalizedName := "fun-io-zstd",
  )
