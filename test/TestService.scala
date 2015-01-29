import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Logger
import play.api.libs.json.{JsNumber, JsValue, Json}


import play.api.test._
import play.api.test.Helpers._

import models.{ServiceError, ClickatellService, NexmoService}

/**
 * Created by Khrupalik on 29.01.2015.
 */
@RunWith(classOf[JUnitRunner])
class TestService extends Specification{

  class TestEnviroment extends ClickatellService

  "afw" should {
    "in" in {
//      running(FakeApplication()) {
//        (new TestEnviroment).send("380961958794", "hi")
//        Thread.sleep(5000)
//        true mustEqual true
//      }
      val json :JsValue = Json.parse("{\n  \"message-count\":\"1\",\n  \"messages\":[\n    {\n    \"status\":\"2\",\n    \"error-text\":\"Missing from param\"\n    }\n  ]\n}")
      val status = json \ "messages" \\ "status"
      val error_message = json \ "messages" \\ "error-text"
//      Logger.debug("Status: " + status.head.toString)
      println("Status: " + status)
      println("Text: " + error_message)

//      ServiceError(JsNumber(status.head).value.toInt,"awd")

      println(Json.prettyPrint(json))
      true mustEqual true
    }
  }

}
