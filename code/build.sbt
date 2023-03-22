// Globals

ThisBuild / organization := "dev.librecybernetics"
ThisBuild / licenses     := Seq(
  "LicenseRef-Anti-Capitalist Software License-1.4" -> url("https://anticapitalist.software/"),
  "LicenseRef-Cooperative Softawre License"         -> url("https://lynnesbian.space/csl/formatted/"),
  "Parity-7.0.0"                                    -> url("https://paritylicense.com/versions/7.0.0")
)

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/LibreCybernetics/Monorepo"),
    "scm:git@github.com:LibreCybernetics/Monorepo.git"
  )
)

ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / scalaVersion  := Version.scala

val sharedSettings = Seq(
  scalaVersion := Version.scala,
  scalacOptions ++= Seq(
    "-explain",
    "-explain-types",
    // Extra Warnings
    "-deprecation",
    "-feature",
    "-unchecked",
    // Extra flags
    "-Ykind-projector:underscores",
    "-Xfatal-warnings"
  )
)

wartremoverErrors ++= Warts.unsafe

//=================//
// Upstream issues //
//=================//

// Scoverage of JS/Native
// - https://github.com/lampepfl/dotty/issues/15383
// - https://github.com/lampepfl/dotty/issues/16124
// ("org.scoverage" %%% "scalac-scoverage-runtime" % "2.0.8" % Test).cross(CrossVersion.for3Use2_13)

//=========//
// Modules //
//=========//

lazy val network =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .dependsOn(
      `unsigned-core`,
      `unsigned-scalacheck` % Test
    )
    .settings(sharedSettings)
    .settings(
      name := "network",
      libraryDependencies ++= Seq(
        "org.scalatest"     %%% "scalatest"          % Version.scalatest          % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest          % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck % Test
      )
    )

lazy val `social-ontology` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .settings(sharedSettings)
    .settings(
      name := "social-ontology",
      libraryDependencies ++= Seq(
        "org.scalatest"     %%% "scalatest"          % Version.scalatest          % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest          % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck % Test
      )
    )

//======//
// TOML //
//======//

// Aggregation

lazy val tomlVersion = "0.1.0-SNAPSHOT"

lazy val toml =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .in(file("lib/toml/"))
    .enablePlugins(ScalaUnidocPlugin)
    .aggregate(
      `toml-core`,
      `toml-parse`
    )
    .settings(
      version                                    := tomlVersion,
      ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(
        thisProject.value.aggregate*
      )
    )

// Sub-projects

lazy val `toml-core` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/toml/core"))
    .settings(sharedSettings)
    .settings(
      name    := "toml-core",
      version := tomlVersion,
      libraryDependencies ++= Seq(
        "org.typelevel"     %%% "cats-core"       % Version.cats,
        "io.github.cquiroz" %%% "scala-java-time" % Version.scalaJavaTime
      )
    )

lazy val `toml-parse` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/toml/parse"))
    .dependsOn(`toml-core`)
    .settings(sharedSettings)
    .settings(
      name    := "toml-parse",
      version := tomlVersion,
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "cats-parse"         % Version.catsParse,
        "org.scalatest" %%% "scalatest"          % Version.scalatest % Test,
        "org.scalatest" %%% "scalatest-wordspec" % Version.scalatest % Test
      )
    )

lazy val `toml-parse-bench` =
  crossProject(JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/toml/parse/bench"))
    .dependsOn(`toml-parse`)
    .settings(sharedSettings)
    .enablePlugins(JmhPlugin)

lazy val `toml-fabric` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/toml/fabric"))
    .dependsOn(`toml-core`)
    .settings(sharedSettings)
    .settings(
      name := "toml-fabric",
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "fabric-core"        % Version.fabric,
        "org.scalatest" %%% "scalatest"          % Version.scalatest % Test,
        "org.scalatest" %%% "scalatest-wordspec" % Version.scalatest % Test
      )
    )

lazy val `toml-tomltest` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/toml/tomltest"))
    .dependsOn(`toml-fabric`, `toml-parse`)
    .settings(sharedSettings)
    .settings(
      name := "toml-tomltest",
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "cats-effect" % Version.catsEffect,
        "org.typelevel" %%% "mouse"       % Version.mouse,
        "co.fs2"        %%% "fs2-io"      % Version.fs2
      )
    )
    .jsSettings(
      scalaJSUseMainModuleInitializer := true
    )
    .nativeSettings(
      nativeMode := "release-fast",
      nativeGC   := "commix",
      nativeLTO  := "thin"
    )

//==========//
// Unsigned //
//==========//

lazy val `unsigned-core` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Full)
    .in(file("lib/unsigned/core"))
    .settings(sharedSettings)
    .settings(
      name := "unsigned-core",
      libraryDependencies ++= Seq(
        "org.typelevel"     %%% "cats-core"          % Version.cats,
        "org.scalatest"     %%% "scalatest"          % Version.scalatest          % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest          % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck % Test
      )
    )

lazy val `unsigned-scalacheck` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/unsigned/scalacheck"))
    .dependsOn(`unsigned-core` % Compile)
    .settings(sharedSettings)
    .settings(
      name                := "unsigned-scalacheck",
      libraryDependencies +=
        "org.scalacheck" %%% "scalacheck" % Version.scalacheck
    )

lazy val zlib =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .settings(sharedSettings)
    .settings(
      name := "git",
      libraryDependencies ++=
        Seq(
          "org.scodec"        %%% "scodec-core"        % "2.2.1",
          "org.scalatest"     %%% "scalatest"          % Version.scalatest          % Test,
          "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest          % Test,
          "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck % Test
        )
    )
