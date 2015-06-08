name := "go"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "io.spray"   %% "spray-http"  % "1.3.1",
  "org.scalaz" %% "scalaz-core" % "7.1.2"
)

play.Project.playScalaSettings
