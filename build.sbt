// Globals

val sharedSettings = Seq(
  scalaVersion := "3.2.2",
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

lazy val catsVersion = "2.9.0"
lazy val scodecVersion = "2.2.1"

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
    .dependsOn(unsigned)
    .settings(sharedSettings)
    .settings(
      name := "network",
      libraryDependencies ++= Seq(
        "org.scalatest" %%% "scalatest" % scalatestVersion % Test,
        "org.scalatest" %%% "scalatest-wordspec" % scalatestVersion % Test,
        "org.scalatestplus" %%% "scalacheck-1-17" % s"$scalatestVersion.0" % Test,
      ),
    )

lazy val `social-ontology` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .settings(sharedSettings)
    .settings(
      name := "social-ontology",
      libraryDependencies ++= Seq(
        "org.scalatest" %%% "scalatest" % scalatestVersion % Test,
        "org.scalatest" %%% "scalatest-wordspec" % scalatestVersion % Test,
        "org.scalatestplus" %%% "scalacheck-1-17" % s"$scalatestVersion.0" % Test
      )
    )

lazy val unsigned =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Full)
    .settings(sharedSettings)
    .settings(
      name := "unsigned",
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "cats-core" % catsVersion,
        "org.scalatest" %%% "scalatest" % scalatestVersion % Test,
        "org.scalatest" %%% "scalatest-wordspec" % scalatestVersion % Test,
        "org.scalatestplus" %%% "scalacheck-1-17" % s"$scalatestVersion.0" % Test,
      )
    )

lazy val zlib =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .settings(sharedSettings)
    .settings(
      name := "git",
      libraryDependencies ++=
        Seq(
          "org.scodec" %%% "scodec-core" % "2.2.1",
          "org.scalatest" %%% "scalatest" % scalatestVersion % Test,
          "org.scalatest" %%% "scalatest-wordspec" % scalatestVersion % Test,
          "org.scalatestplus" %%% "scalacheck-1-17" % s"$scalatestVersion.0" % Test
        )
    )