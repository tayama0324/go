package controllers

import controllers.PostController.Request
import domain.entity.{Link, LinkAttr, LinkId}
import domain.service.UsesLinkPostService
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, AnyContent, Action, Controller}

/**
 */
abstract class PostController extends Controller with UsesLinkPostService {

  def post(): Action[AnyContent] = Action { implicit request =>
    PostController.form.bindFromRequest().fold(
      { e => ??? }
      { r => postInternal(r) }
    )
  }

  private def postInternal(request: Request): Result = {
    val id = LinkId.of(request.id)
    val attr = LinkAttr(request.destination, request.owner)
    val link = Link(id, attr)
    linkPostService.insert(link) match {
      case true => ??? // success
      case false => ??? // failureぎ
    }
  }
}


object PostController {

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
