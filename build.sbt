import sbt._
import Keys._

name := "go"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "ch.qos.logback"    %  "logback-classic"   % "1.1.3",
  "com.typesafe.play" %% "play-json"         % "2.4.2",
  "io.spray"          %% "spray-http"        % "1.3.1",
  "org.mongodb"       %% "casbah"            % "2.8.2",
  "org.scalaz"        %% "scalaz-core"       % "7.1.2"
)

lazy val root = (project in file("."))
  .enablePlugins(play.PlayScala)
