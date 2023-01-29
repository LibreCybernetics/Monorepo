val sharedSettings = Seq(
  scalaVersion := "3.2.1",
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
