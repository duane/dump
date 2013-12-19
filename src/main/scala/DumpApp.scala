package com.github.duane.dump

import com.twitter.finagle.stats.NullStatsReceiver
import com.twitter.cassie.Cluster

object DumpApp extends Application {
  val cluster = new Cluster("localhost", NullStatsReceiver)
  val keyspace = cluster.keyspace("crawler").connect()
}
