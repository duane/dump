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
      scalaVersion := "2.8.1",
      resolvers ++= Seq(
        "Twitter's Repository" at "http://maven.twttr.com/",
        Classpaths.typesafeReleases
      ),
      libraryDependencies ++= Seq(
        "com.twitter" % "cassie" % "0.20.0" exclude("javax.jms", "jms") exclude("com.sun.jmx","jmxri" ) exclude("com.sun.jdmk", "jmxtools"),
        "thrift" % "libthrift" % "0.5.0" from "http://maven.twttr.com/thrift/libthrift/0.5.0/libthrift-0.5.0.jar"
      )
    )
  ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
}
