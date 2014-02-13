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
        Classpaths.typesafeReleases,
        "RethinkScala Repository" at "http://kclay.github.io/releases"
      ),
      libraryDependencies ++= Seq(
        "com.rethinkscala" %% "core" % "0.4",
        "com.datastax.cassandra" % "cassandra-driver-core" % "2.0.0-rc2",
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.0.2",
        "ch.qos.logback" % "logback-classic" % "1.0.13",
        "org.json4s" %% "json4s-jackson" % "3.2.6",
        "org.scalaj" %% "scalaj-http" % "0.3.12",
        "net.jpountz.lz4" % "lz4" % "1.2.0",
        "org.xerial.snappy" % "snappy-java" % "1.1.1-M1",
        "org.scala-lang" % "scala-actors" % "2.10.1"
      ),
      scalacOptions ++= Seq (
        "-deprecation",
        "-feature",
        "-language:postfixOps"
      )
    )
  ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
}
