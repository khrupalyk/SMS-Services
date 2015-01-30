package models

import java.net.URLEncoder

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

abstract class ServiceError

case class NexmoError(status: Int, text_error: String) extends ServiceError

case class ClickatellError(status: Int, text_error: String) extends ServiceError

trait SMSService {
  def send(mobile: String, message: String): Future[Either[ServiceError, String]]
}

trait ClickatellService extends SMSService {

  override def send(mobile: String, message: String): Future[Either[ServiceError, String]] = {

    val config = ConfigFactory.load()
    val apiId = config.getString("clickatell.api_id")
    val user = config.getString("clickatell.user")
    val password = config.getString("clickatell.password")

    val request: String = ClickatellRequestTemplate
      .setApiId(apiId)
      .setUser(user)
      .setMessage(message)
      .setPassword(password)
      .setMobile(mobile)
      .build()

    val response = WS.url(request).get()

    response.map(resp =>
      if (resp.body.contains("OK")) {
        Right(resp.body.replace("OK:", "").trim)
      } else {
        val body: Array[String] = resp.body.split(",")
        Left(ClickatellError(body(0).replace("ERR:", "").trim.toInt, body(1).trim))
      }
    )

  }

  class ClickatellRequestTemplate {
    private var host: String = "http://api.clickatell.com/http/sendmsg"
    private var apiId: String = ""
    private var user: String = ""
    private var password: String = ""
    private var message: String = ""
    private var from: String = "SmartChat"
    private var phone: String = ""

    def build(): String = {
      host + "?user=" + user + "&password=" +
        password + "&from=" + from + "&api_id=" + apiId + "&text=" + URLEncoder.encode(message, "UTF-8") + "&to=" + phone
    }

    def setApiId(data: String): ClickatellRequestTemplate = {
      this.apiId = data
      this
    }

    def setUser(data: String): ClickatellRequestTemplate = {
      this.user = data
      this
    }

    def setPassword(data: String): ClickatellRequestTemplate = {
      this.password = data
      this
    }

    def setHost(data: String): ClickatellRequestTemplate = {
      this.host = data
      this
    }

    def setMobile(data: String): ClickatellRequestTemplate = {
      this.phone = data
      this
    }

    def setMessage(data: String): ClickatellRequestTemplate = {
      this.message = data
      this
    }

    def setTitle(data: String): ClickatellRequestTemplate = {
      this.from = data
      this
    }
  }

  object ClickatellRequestTemplate extends ClickatellRequestTemplate

}

trait NexmoService extends SMSService {

  val config = ConfigFactory.load()

  override def send(mobile: String, message: String): Future[Either[ServiceError, String]] = {

    val apiKey = config.getString("nexmo.api_key")
    val apiSecret = config.getString("nexmo.api_secret")

    val request = NexmoRequestTemplate
      .setApiKey(apiKey)
      .setApiSecret(apiSecret)
      .setDataType("json")
      .setMessage(message)
      .setMobile(mobile)
      .build()

    val response: Future[JsValue] = WS.url(request).withRequestTimeout(5000).get().map(
      response => response.json
    )

    response.map(json => json \ "messages").map {
      js => {
        val code = (js \\ "status").toList(0).toString().replace("\"", "").toInt
        if (code != 0) {
          val errorText = (js \\ "error-text").toList(0).toString().replace("\"", "")
          Left(NexmoError(code, errorText))
        } else {
          Right((js \\ "message-id").toList(0).toString().replace("\"", ""))
        }
      }
    }
  }

  class NexmoRequestTemplate {
    private var host: String = "https://rest.nexmo.com/sms/"
    private var dataType: String = "json"
    private var apiKey: String = ""
    private var apiSecret: String = ""
    private var phone: String = ""
    private var message: String = ""
    private var from: String = "SmartChat"

    def setTitle(data: String): NexmoRequestTemplate = {
      this.from = data
      this
    }

    def setMobile(data: String): NexmoRequestTemplate = {
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
        apiSecret + "&from=" + from + "&to=" + phone + "&text=" + URLEncoder.encode(message, "UTF-8")
    }

  }

  object NexmoRequestTemplate extends NexmoRequestTemplate

}