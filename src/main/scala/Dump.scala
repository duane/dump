import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

case class Story(author: String, title: String)

object Dump extends App {
  val contents = "{\"data\":{\"children\":[{\"data\":{\"author\":\"duane\",\"title\":\"the title\"}},{\"data\":{\"author\":\"bailey\",\"title\":\"the title\"}}]}}"
  val json = parse(contents)
  
  println(pretty(render((for {
    JObject(all) <- json
    JField("data", JObject(data)) <- all
    JField("children", JArray(children)) <- data
    JObject(child_wrap) <- children
    JField("data", JObject(story)) <- child_wrap
  } yield(story)) map list2jvalue)))
}