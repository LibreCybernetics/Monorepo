// Globals

val sharedSettings = Seq(
  scalaVersion := "3.2.2",
  scalacOptions ++= Seq(
    "-deprecation",
    "-explain",
    "-explain-types",
    "-feature",
    "-unchecked",
    "-Xfatal-warnings"
  )
)

wartremoverErrors ++= Warts.unsafe

lazy val globalDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.scalatest" %% "scalatest-wordspec" % "3.2.15" % Test,
  "org.scalatestplus" %% "scalacheck-1-17" % "3.2.15.0" % Test
)

// Modules

lazy val network =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .settings(sharedSettings)
    .settings(
      name := "network",
      libraryDependencies ++= globalDependencies
    )

lazy val `social-ontology` =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .settings(sharedSettings)
    .settings(
      name := "social-ontology",
      libraryDependencies ++= globalDependencies
    )

lazy val unsigned =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Full)
    .settings(sharedSettings)
    .settings(
      name := "unsigned",
      libraryDependencies ++= globalDependencies
    )

lazy val zlib =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .settings(sharedSettings)
    .settings(
      name := "git",
      libraryDependencies ++= globalDependencies ++
        Seq(
          "org.scodec" %%% "scodec-core" % "2.2.1"
        )
    )