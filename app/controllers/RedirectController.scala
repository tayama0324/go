package controllers

import controllers.renderer.{MixInFormRenderer, UsesFormRenderer}
import domain.service.{MixInRedirectService, UsesRedirectService}
import play.api.mvc.{Action, Controller, Results}
import spray.http.Uri
import spray.http.Uri.Path.Segment

import scala.util.control.NonFatal
import scalaz.{-\/, \/-}

/**
 */
abstract class RedirectController extends Controller with UsesRedirectService with UsesFormRenderer {

  def redirect(pathString: String) = Action { implicit request =>
    val path = Uri.Path(pathString)
    redirectService.resolve(path) match {
      case \/-(uri) => Results.Redirect(uri.toString())
      case -\/(e: NoSuchElementException) =>
        val candidate = path match {
          case Segment(head, _) => head
          case _ => ""
        }
        formRenderer.form(candidate)
      case -\/(NonFatal(e)) =>
        e.printStackTrace()
        formRenderer.form("")
    }
  }
}

object RedirectController
  extends RedirectController
  with MixInRedirectService
  with MixInFormRenderer
