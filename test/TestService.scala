import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner


import play.api.test._
import play.api.test.Helpers._

import models.{ClickatellService, NexmoService}

/**
 * Created by Khrupalik on 29.01.2015.
 */
@RunWith(classOf[JUnitRunner])
class TestService extends Specification{

  class TestEnviroment extends ClickatellService

  "afw" should {
    "in" in {
      running(FakeApplication()) {
        (new TestEnviroment).send("380961958794", "hi")
        Thread.sleep(5000)
        true mustEqual true
      }
    }
  }

}
