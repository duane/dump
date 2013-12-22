import sbt._
import Keys._

object build extends Build {

  val gcsettings = Defaults.defaultSettings

  val gc = TaskKey[Unit]("gc", "runs garbage collector")
  val gcTask = gc := {
    println("requesting garbage collection")
    System gc()
  }

  lazy val project = Project (
    "dump",
    file("."),
    settings = gcsettings ++ Seq(
      gcTask,
      scalaVersion := "2.10.1",
      resolvers ++= Seq(
        Classpaths.typesafeReleases
      ),
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-actor" % "2.2.3",
        "com.typesafe.akka" %% "akka-slf4j" % "2.2.3",
        "com.datastax.cassandra"  % "cassandra-driver-core" % "1.0.1" exclude("org.slf4j", "slf4j-log4j12"),
        "ch.qos.logback" % "logback-classic" % "1.0.13",
        "org.json4s" %% "json4s-native" % "3.2.6"
      ),
      scalacOptions ++= Seq (
        "-deprecation",
        "-feature",
        "-language:postfixOps"
      )
    )
  ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
}
