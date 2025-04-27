val scalaLatestLTS = "3.3.5"
val scalaLatest = "3.7.0-RC4"
val scalaVersions = Seq(scalaLatestLTS, scalaLatest)

ThisBuild / scalaVersion := scalaLatestLTS

lazy val core = (projectMatrix in file("core"))
  .settings(
    name := "money-core",
  )
  .jvmPlatform(scalaVersions = scalaVersions)
  .jsPlatform(scalaVersions = scalaVersions)
  .nativePlatform(scalaVersions = scalaVersions)
