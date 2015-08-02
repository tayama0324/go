package controllers

import controllers.DeleteController.Request
import controllers.renderer.MixInFormRenderer
import controllers.renderer.UsesFormRenderer
import domain.entity.LinkId
import domain.service.LinkDeleteService.DeleteResult
import domain.service.MixInLinkDeleteService
import domain.service.UsesLinkDeleteService
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.single
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

/**
 * Created by takashi_tayama on 2015/06/28.
 */
abstract class DeleteController
  extends Controller
  with UsesLinkDeleteService
  with UsesFormRenderer {

  def delete(): Action[AnyContent] = Action { implicit request =>
    DeleteController.form.bindFromRequest().fold(
      { e => formRenderer.onError() },
      { r => deleteInternal(r) }
    )
  }

  private def deleteInternal(request: Request): Result = {
    val result = for {
      id <- LinkId.of(request.id)
      result = linkDeleteService.delete(id, request.owner)
    } yield result

    result match {
      case Some(DeleteResult.Succeeded) =>
        formRenderer.onDeleted(request.id)

      case Some(DeleteResult.NoSuchLink) =>
        formRenderer.onError(Some("このキーワードは登録されていません。"))

      case Some(DeleteResult.OwnerMismatch) =>
        formRenderer.onError(Some("キーワードの所有者の名前が違います。"))

      case None =>
        formRenderer.onError()
    }
  }

  def forceDelete(): Action[AnyContent] = Action { implicit request =>
    DeleteController.forceDeleteForm.bindFromRequest().fold(
      { e => formRenderer.onError() },
      { r => forceDeleteInternal(r) }
    )
  }

  private def forceDeleteInternal(id: String): Result = {
    val result = for {
      id <- LinkId.of(id)
      result = linkDeleteService.forceDelete(id)
    } yield result

    result match {
      case Some(DeleteResult.Succeeded) =>
        formRenderer.onDeleted(id)

      case Some(DeleteResult.NoSuchLink) =>
        formRenderer.onError(Some("このキーワードは登録されていません。"))

      case Some(DeleteResult.OwnerMismatch) =>
        // Unreachable
        formRenderer.onError(Some("キーワードの所有者の名前が違います。"))

      case None =>
        formRenderer.onError()
    }
  }
}


object DeleteController
  extends DeleteController
  with MixInLinkDeleteService
  with MixInFormRenderer {

  case class Request(
    id: String,
    owner: String
  )

  val form: Form[Request] = Form(
    mapping(
      "id" -> text,
      "owner" -> text
    )(Request.apply)(Request.unapply)
  )

  val forceDeleteForm: Form[String] = Form[String](
    single("id" -> text)
  )
}
