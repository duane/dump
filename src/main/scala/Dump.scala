import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import scalaj.http._
import scala.actors.Actor
import scala.actors.Actor._
import com.datastax.driver.core._
import scala.concurrent.duration._

case class Story(author: String,
                  title: String,
            created_utc: String,
                 domain: String,
                    ups: Int,
                  downs: Int,
              subreddit: String,
                    url: String,
                  score: Int,
                is_self: Boolean,
                     id: String,
                   name: String,
               selftext: String) {
  def getComments() Option[List[Comment]] {
    val url = "http://www.reddit.com/%s/comments/%s.json".format(subreddit, id)
    val (responseCode, headersMap, resultString) = Http(url).option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).header("User-Agent", "dump v0.0.1 by bailey.d.r at gmail.com").asHeadersAndParse(Http.readString)

    if (responseCode != 200)
      None
    else
      Some(for {
        JObject(all) <- parse(resultString)
        JField("data", JObject(data)) <- all
        JField("children", JArray(children)) <- data
        JObject(child_wrap) <- children
        JField("data", comment) <- child_wrap
      } yield(story.extract[Comment]))
  }
}

object Dump extends App {
  def subreddit_listing(subreddit: String): Option[List[Story]] = {
    val url = "http://www.reddit.com/r/%s/new.json".format(subreddit)
    val (responseCode, headersMap, resultString) = Http(url).option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).header("User-Agent", "dump v0.0.1 by bailey.d.r at gmail.com").asHeadersAndParse(Http.readString)

    if (responseCode != 200)
      None
    else
      Some(for {
        JObject(all) <- parse(resultString)
        JField("data", JObject(data)) <- all
        JField("children", JArray(children)) <- data
        JObject(child_wrap) <- children
        JField("data", story) <- child_wrap
      } yield(story.extract[Story]))
  }

  implicit val formats = DefaultFormats
  val cluster: Cluster = Cluster.builder().addContactPoint("127.0.0.1").build()

  val writer = actor {
    val session: Session = cluster.connect("crawler")
    val statement = session.prepare(
      "INSERT INTO stories (author, title, created_utc, domain, ups, downs, subreddit, url, score, is_self, id, name, selftext) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);"
    )
    val boundStatement = new BoundStatement(statement)
    while (true) {
      receive {
        case story: Story => {
          session.execute(boundStatement.bind(
            story.author,
            story.title,
            story.created_utc,
            story.domain,
            story.ups: java.lang.Integer,
            story.downs: java.lang.Integer,
            story.subreddit,
            story.url,
            story.score: java.lang.Integer,
            story.is_self: java.lang.Boolean,
            story.id,
            story.name
          ))
          println("Wrote story %s from \"%s\": %s".format(story.name, story.subreddit, story.title))
        }
      }
    }
  }
  while (true) {
    subreddit_listing("all") map {
      _.foreach(writer ! _)
    }
    Thread.sleep(30000)
  }
}