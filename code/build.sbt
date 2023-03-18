// Globals

lazy val Scala3Version = "3.2.2"

ThisBuild / scalaVersion := Scala3Version
ThisBuild / organization := "dev.librecybernetics"

val sharedSettings = Seq(
  scalaVersion := Scala3Version,
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

//==========//
// Versions //
//==========//

// Typelevel Deps

lazy val catsVersion          = "2.9.0"
lazy val catsEffectVersion    = "3.4.8"
lazy val catsParseVersion     = "0.3.9"
lazy val fs2Version           = "3.6.1"
lazy val fabricVersion        = "1.10.3"
lazy val mouseVersion         = "1.2.1"
lazy val scalacheckVersion    = "1.17.0"
lazy val scalaJavaTimeVersion = "2.5.0"
lazy val scodecVersion        = "2.2.1"

// Other Deps

lazy val scalatestVersion = "3.2.15"

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
        "org.scalatest"     %%% "scalatest"          % scalatestVersion       % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % scalatestVersion       % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % s"$scalatestVersion.0" % Test
      )
    )

lazy val `social-ontology` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .settings(sharedSettings)
    .settings(
      name := "social-ontology",
      libraryDependencies ++= Seq(
        "org.scalatest"     %%% "scalatest"          % scalatestVersion       % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % scalatestVersion       % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % s"$scalatestVersion.0" % Test
      )
    )

lazy val `toml-core` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/toml/core"))
    .settings(sharedSettings)
    .settings(
      name := "toml-core",
      libraryDependencies ++= Seq(
        "org.typelevel"     %%% "cats-core"       % catsVersion,
        "io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeVersion
      )
    )

lazy val `toml-parse` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lib/toml/parse"))
    .dependsOn(`toml-core`)
    .settings(sharedSettings)
    .settings(
      name := "toml-parse",
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "cats-parse"         % catsParseVersion,
        "org.scalatest" %%% "scalatest"          % scalatestVersion % Test,
        "org.scalatest" %%% "scalatest-wordspec" % scalatestVersion % Test
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
        "org.typelevel" %%% "fabric-core"        % fabricVersion,
        "org.scalatest" %%% "scalatest"          % scalatestVersion % Test,
        "org.scalatest" %%% "scalatest-wordspec" % scalatestVersion % Test
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
        "org.typelevel" %%% "cats-effect" % catsEffectVersion,
        "org.typelevel" %%% "mouse"       % mouseVersion,
        "co.fs2"        %%% "fs2-io"      % fs2Version
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

lazy val `unsigned-core` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Full)
    .in(file("lib/unsigned/core"))
    .settings(sharedSettings)
    .settings(
      name := "unsigned-core",
      libraryDependencies ++= Seq(
        "org.typelevel"     %%% "cats-core"          % catsVersion,
        "org.scalatest"     %%% "scalatest"          % scalatestVersion       % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % scalatestVersion       % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % s"$scalatestVersion.0" % Test
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
        "org.scalacheck" %%% "scalacheck" % scalacheckVersion
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
          "org.scalatest"     %%% "scalatest"          % scalatestVersion       % Test,
          "org.scalatest"     %%% "scalatest-wordspec" % scalatestVersion       % Test,
          "org.scalatestplus" %%% "scalacheck-1-17"    % s"$scalatestVersion.0" % Test
        )
    )
