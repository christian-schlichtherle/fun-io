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
import Dependencies._
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

object BuildSettings {

  lazy val releaseSettings: Seq[Setting[_]] = {
    Seq(
      releaseCrossBuild := false,
      releaseProcess := Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        releaseStepCommandAndRemaining("+test"),
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        releaseStepCommandAndRemaining("+publishSigned"),
        setNextVersion,
        commitNextVersion,
        pushChanges,
      ),
    )
  }

  private lazy val commonSettings: Seq[Setting[_]] = {
    Seq(
      homepage := Some(url("https://christian-schlichtherle.github.com/fun-io/")),
      licenses := Seq("Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
      organization := "global.namespace.fun-io",
      organizationHomepage := Some(new URL("http://schlichtherle.de")),
      organizationName := "Schlichtherle IT Services",
      pomExtra := {
        <developers>
          <developer>
            <name>Christian Schlichtherle</name>
            <email>christian AT schlichtherle DOT de</email>
            <organization>Schlichtherle IT Services</organization>
            <timezone>4</timezone>
            <roles>
              <role>owner</role>
            </roles>
            <properties>
              <picUrl>http://www.gravatar.com/avatar/e2f69ddc944f8891566fc4b18518e4e6.png</picUrl>
            </properties>
          </developer>
        </developers>
        <issueManagement>
          <system>Github</system>
          <url>https://github.com/christian-schlichtherle/fun-io/issues</url>
        </issueManagement>
      },
      pomIncludeRepository := (_ => false),
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        Some(
          if (version(_ endsWith "-SNAPSHOT").value) {
            "snapshots" at nexus + "content/repositories/snapshots"
          } else {
            "releases" at nexus + "service/local/staging/deploy/maven2"
          }
        )
      },
      scalaVersion := ScalaVersion_2_13, // set here or otherwise `+publishSigned` will fail
      scmInfo := Some(ScmInfo(
        browseUrl = url("https://github.com/christian-schlichtherle/fun-io"),
        connection = "scm:git:git@github.com:christian-schlichtherle/fun-io.git",
        devConnection = Some("scm:git:git@github.com:christian-schlichtherle/fun-io.git")
      )),
    )
  }

  lazy val aggregateSettings: Seq[Setting[_]] = {
    commonSettings ++ Seq(
      crossPaths := false,
      crossScalaVersions := Seq.empty,
      publish / skip := true,
    )
  }

  lazy val artifactSettings: Seq[Setting[_]] = {
    commonSettings ++ Seq(
      logBuffered := false, // http://www.scalatest.org/user_guide/using_scalatest_with_sbt
      testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),
    )
  }

  lazy val librarySettings: Seq[Setting[_]] = {
    artifactSettings ++ Seq(
      // Support testing Java projects with ScalaTest et al:
      compileOrder := CompileOrder.JavaThenScala,
      javacOptions := DefaultOptions.javac ++ Seq(Opts.compile.deprecation, "-Xlint", "-source", "1.8", "-target", "1.8", "-g"),
      javacOptions in doc := DefaultOptions.javac ++ Seq("-source", "1.8"),
      packageOptions in(Compile, packageBin) += Package.ManifestAttributes("Automatic-Module-Name" ->
        ("global.namespace." + normalizedName.value.replace('-', '.'))
      ),
      scalacOptions := DefaultOptions.scalac ++ Seq(Opts.compile.deprecation, "-feature", Opts.compile.unchecked, "-target:jvm-1.8"),
    )
  }

  lazy val javaLibrarySettings: Seq[Setting[_]] = {
    librarySettings ++ Seq(
      autoScalaLibrary := false,
      crossPaths := false,
      crossScalaVersions := Seq(scalaVersion.value),
    )
  }

  lazy val scalaLibrarySettings: Seq[Setting[_]] = {
    librarySettings ++ Seq(
      crossScalaVersions := Seq(ScalaVersion_2_10, ScalaVersion_2_11, ScalaVersion_2_12, ScalaVersion_2_13),
    )
  }
}
