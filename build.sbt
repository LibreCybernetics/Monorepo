ThisBuild / scalaVersion := "3.2.1"

lazy val globalDependencies = Seq()

lazy val network =
  project
    .settings(
      name := "network",
      libraryDependencies ++= globalDependencies
    )
