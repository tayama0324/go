package controllers

import controllers.PostController.Request
import controllers.renderer.MixInFormRenderer
import controllers.renderer.UsesFormRenderer
import domain.entity.Link
import domain.entity.LinkAttr
import domain.entity.LinkId
import domain.service.LinkPostService
import domain.service.UsesLinkPostService
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import scalaz.-\/
import scalaz.\/-

/**
 */
abstract class PostController
  extends Controller
  with UsesLinkPostService
  with UsesFormRenderer {

  def get(): Action[AnyContent] = Action { _ =>
    formRenderer.form()
  }

  def post(): Action[AnyContent] = Action { implicit request =>
    PostController.form.bindFromRequest().fold(
      { e => formRenderer.onError() },
      { r => postInternal(r) }
    )
  }

  private def postInternal(request: Request): Result = {
    val result = for {
      id <- LinkId.of(request.id)
      attr = LinkAttr(request.destination, request.owner)
      link = Link(id, attr)
      result = linkPostService.insert(link)
    } yield result

    result match {
      case Some(\/-(_)) =>
        formRenderer.onRegistered(request.id)

      case Some(-\/(LinkPostService.Error.AlreadyRegistered)) =>
        formRenderer.onError(Some("このキーワードは既に登録済みです。"))

      case Some(-\/(LinkPostService.Error.Reserved)) =>
        formRenderer.onError(Some("このキーワードは予約されています。"))

      case None =>
        formRenderer.onError()
    }
  }
}

object PostController
  extends PostController
  with domain.service.MixInLinkPostService
  with MixInFormRenderer {

  case class Request(
    id: String,
    destination: String,
    owner: String
  )

  val form: Form[Request] = Form(
    mapping(
      "id" -> text,
      "destination" -> text,
      "owner" -> text
    )(Request.apply)(Request.unapply)
  )
}
