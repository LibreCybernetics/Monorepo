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

ThisBuild / versionScheme     := Some("semver-spec")
ThisBuild / scalaVersion      := Version.scala
Global / onChangedBuildSource := ReloadOnSourceChanges

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
    "-language:implicitConversions",
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
    .in(file("lib/network/"))
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

lazy val `parse-utils` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/parse-utils/"))
    .settings(sharedSettings)
    .settings(
      name := "parse-utils",
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "cats-parse" % Version.catsParse
      )
    )

lazy val rfc3339 =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/rfc3339/"))
    .dependsOn(`parse-utils`)
    .settings(sharedSettings)
    .settings(
      name := "rfc3339",
      libraryDependencies ++= Seq(
        "io.github.cquiroz" %%% "scala-java-time" % Version.scalaJavaTime
      )
    )

lazy val rfc4648 =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/rfc4648/"))
    .settings(sharedSettings)
    .settings(
      name := "rfc4648",
      libraryDependencies ++= Seq(
        "org.scalatest"     %%% "scalatest"          % Version.scalatest          % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest          % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck % Test
      )
    )

lazy val `social-ontology` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/social-ontology/"))
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
    .crossType(CrossType.Pure)
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
        "org.typelevel"     %%% "cats-core"          % Version.cats,
        "io.github.cquiroz" %%% "scala-java-time"    % Version.scalaJavaTime,
        "org.scalatest"     %%% "scalatest"          % Version.scalatest % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest % Test
      )
    )

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

lazy val `toml-parse` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/toml/parse"))
    .dependsOn(rfc3339, `toml-core`)
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

//========//
// uuidv7 //
//========//

lazy val uuidv7 =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/uuidv7/"))
    .settings(sharedSettings)
    .settings(
      name := "typeid",
      libraryDependencies ++= Seq(
        "org.typelevel"     %%% "cats-effect"        % Version.catsEffect,
        "io.github.cquiroz" %%% "scala-java-time"    % Version.scalaJavaTime,
        "org.scalatest"     %%% "scalatest"          % Version.scalatest          % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest          % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck % Test
      )
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
    .in(file("lib/zlib/"))
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

//==========//
// Fugitiva //
//==========//

lazy val fugitiva =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("app/fugitiva"))
    .settings(sharedSettings)
    .settings(
      name                := "fugitiva",
      libraryDependencies ++=
        Seq(
          "com.lihaoyi"   %%% "scalatags"                     % Version.scalatags,
          ("io.getquill"   %% "quill-jasync-postgres"         % Version.protoquill)
            .exclude("org.scalameta", "scalafmt-core_2.13"),
          "org.http4s"    %%% "http4s-ember-client"           % Version.http4s,
          "org.http4s"    %%% "http4s-ember-server"           % Version.http4s,
          "org.http4s"    %%% "http4s-dsl"                    % Version.http4s,
          "org.http4s"    %%% "http4s-scalatags"              % Version.http4sScalatags,
          "org.scalatest" %%% "scalatest"                     % Version.scalatest         % Test,
          "org.scalatest" %%% "scalatest-wordspec"            % Version.scalatest         % Test,
          "org.typelevel"  %% "cats-effect-testing-scalatest" % Version.catsEffectTesting % Test
        ),
      reStart / mainClass := Some("coop.fugitiva.Backend")
    )
