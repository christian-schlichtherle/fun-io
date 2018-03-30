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
import BuildSettings._
import Dependencies._

lazy val root: Project = project
  .in(file("."))
  .aggregate(api, bios, commonsCompress, it, jackson, jaxb, scalaApi, xz, zip)
  .settings(releaseSettings)
  .settings(aggregateSettings)
  .settings(name := "Fun I/O")

lazy val api: Project = project
  .in(file("api"))
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      MockitoCore % Test,
      Scalatest % Test
    ),
    name := "Fun I/O API",
    normalizedName := "fun-io-api"
  )

lazy val bios: Project = project
  .in(file("bios"))
  .dependsOn(api)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      MockitoCore % Test,
      Scalatest % Test
    ),
    name := "Fun I/O Basic",
    normalizedName := "fun-io-bios"
  )

lazy val commonsCompress: Project = project
  .in(file("commons-compress"))
  .dependsOn(bios)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      CommonsCompress,
      Xz % Optional
    ),
    name := "Fun I/O Commons Compress",
    normalizedName := "fun-io-commons-compress"
  )

lazy val it: Project = project
  .in(file("it"))
  .dependsOn(bios, commonsCompress, jackson, jaxb, scalaApi, xz)
  .settings(scalaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      MockitoCore % Test,
      Scalacheck % Test,
      Scalatest % Test
    ),
    name := "Fun I/O IT",
    normalizedName := "fun-io-it",
    publishArtifact := false
  )

lazy val jackson: Project = project
  .in(file("jackson"))
  .dependsOn(api)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      JacksonDatabind
    ),
    name := "Fun I/O Jackson",
    normalizedName := "fun-io-jackson"
  )

lazy val jaxb: Project = project
  .in(file("jaxb"))
  .dependsOn(api)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      ActivationApi,
      JaxbApi,
      JaxbRuntime
    ),
    name := "Fun I/O JAXB",
    normalizedName := "fun-io-jaxb"
  )

lazy val scalaApi: Project = project
  .in(file("scala-api"))
  .dependsOn(api)
  .settings(scalaLibrarySettings)
  .settings(
    name := "Fun I/O Scala API",
    normalizedName := "fun-io-scala-api"
  )

lazy val xz: Project = project
  .in(file("xz"))
  .dependsOn(bios)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      Xz
    ),
    name := "Fun I/O XZ",
    normalizedName := "fun-io-xz"
  )

lazy val zip: Project = project
  .in(file("zip"))
  .dependsOn(jaxb)
  .settings(javaLibrarySettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.google.code.findbugs" % "annotations" % "3.0.0",
      MockitoCore % Test,
      Scalacheck % Test,
      Scalatest % Test
    ),
    name := "Fun I/O ZIP",
    normalizedName := "fun-io-zip"
  )
