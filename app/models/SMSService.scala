package models

import com.typesafe.config.ConfigFactory

import play.api.Logger
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSResponse, WS}
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org._


/**
 * Created by Khrupalik on 29.01.2015.
 */

case class ErrorElement(status: String, text: String)

case class ServiceError(count: String, element: Array[ErrorElement])


trait SMSService {
  def send(mobile: String, message: String): Future[Either[ServiceError, String]]
}

trait ClickatellService extends SMSService {
  def send(mobile: String, message: String): Future[Either[ServiceError, String]] = {
    //    val smsSender = SmsSender.getClickatellSender("khrupalik", "QfAJAIafHRcFXO", "3523298")
    //    val msg = message
    //    val reciever = mobile
    //    val sender = "SmartChat"
    //    smsSender.connect()
    //    val msgids: String = smsSender.sendTextSms(msg, reciever, sender)
    //    smsSender.disconnect()
    //    println("Id: " + msgids)


    val request = "http://api.clickatell.com/http/sendmsg?user=khrupalik&password=QfAJAIafHRcFXO&api_id=3523298&to=380961958794&text=Message"


    val response: Future[WSResponse] = WS.url(request).get()

    Logger.debug("Response: " + response.map(resp => resp.json(1)))

    Future(Right("awd"))
  }
}

trait NexmoService extends SMSService {

  val config = ConfigFactory.load()
  //  val template = S"https://rest.nexmo.com/sms/$type"

  override def send(mobile: String, message: String): Future[Either[ServiceError, String]] = {


    val request = NexmoRequestTemplate.setApiKey("53fbbbe0")
      .setApiSecret("bb911ac1")
      .setDataType("json")
      .setMessage(message)
      .setPhone(mobile)
      .build()



    val response: Future[JsValue] = WS.url(request).get().map(
      response => response.json
    )

    val status = response.map(json => json \ "messages" \\ "status")

    Future(Right("id"))
  }

  class NexmoRequestTemplate {
    private var host: String = "https://rest.nexmo.com/sms/"
    private var dataType: String = "json"
    private var apiKey: String = ""
    private var apiSecret: String = ""
    private var phone: String = ""
    private var message: String = ""
    private var from: String = "SmartChat"

    def setPhone(data: String): NexmoRequestTemplate = {
      this.phone = data
      this
    }

    def setMessage(data: String): NexmoRequestTemplate = {
      this.message = data
      this
    }

    def setDataType(data: String): NexmoRequestTemplate = {
      this.dataType = data
      this
    }

    def setApiKey(key: String): NexmoRequestTemplate = {
      apiKey = key
      this
    }

    def setApiSecret(key: String): NexmoRequestTemplate = {
      apiSecret = key
      this
    }

    def setHost(key: String): NexmoRequestTemplate = {
      host = key
      this
    }

    def build(): String = {
      host + dataType + "?api_key=" + apiKey + "&api_secret=" +
        apiSecret + "&from=" + from + "&to=" + phone + "&text=" + message
    }


  }

  object NexmoRequestTemplate extends NexmoRequestTemplate

}