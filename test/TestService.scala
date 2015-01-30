import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Logger
import play.api.libs.json.{JsValue, JsNumber, Json}


import play.api.test._
import play.api.test.Helpers._

import models.{ServiceError, ClickatellService, NexmoService}

import scala.concurrent.Future

/**
 * Created by Khrupalik on 29.01.2015.
 */
@RunWith(classOf[JUnitRunner])
class TestService extends Specification {

  class TestEnviroment extends ClickatellService

  "afw" should {
    "in" in {
      running(FakeApplication()) {
        val response: Future[Either[ServiceError, String]] = (new TestEnviroment).send("380961958794", "hi aiwudg a    awd")
        println(response.map(res => println(res)))
        Thread.sleep(5000)
        true mustEqual true

        true mustEqual true
      }
    }
  }

}
