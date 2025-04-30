val scalaLatestLTS = "3.3.5"
val scalaLatest = "3.7.0-RC4"
val scalaVersions = Seq(scalaLatestLTS, scalaLatest)

ThisBuild / scalaVersion := scalaLatestLTS

lazy val core = (projectMatrix in file("core"))
  .settings(
    name := "money-core",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.13.0"
    )
  )
  .jvmPlatform(scalaVersions = scalaVersions)
  .jsPlatform(scalaVersions = scalaVersions)
  .nativePlatform(scalaVersions = scalaVersions)
