package controllers

import play.api.GlobalSettings
import play.api.mvc.RequestHeader
import play.api.mvc.Results
import scala.concurrent.Future

/**
 * Created by takashi_tayama on 2015/08/02.
 */
object Global extends GlobalSettings {
  override def onError(request: RequestHeader, ex: Throwable) = {
    println("An error occurred.")
    ex.printStackTrace()
    Future.successful(Results.InternalServerError("Error: " + ex.getMessage))
  }
}
