package controllers

import domain.entity.Link
import domain.service.MixInLinkBackupService
import domain.service.UsesLinkBackupService
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Results

/**
 * Created by takashi_tayama on 2015/06/26.
 */
abstract class ManagementController
  extends Controller
  with UsesLinkBackupService {

  def dump(): Action[AnyContent] = Action { _ =>
    Results.Ok(Json.toJson(linkBackupService.dump().toSeq))
  }

  def restore(): Action[AnyContent] = Action { request =>
    request.body.asJson.map(Json.fromJson[Seq[Link]](_)) match {
      case Some(JsSuccess(links, _)) =>
        linkBackupService.restore(links)
        Results.Ok("ok")
      case _ =>
        Results.BadRequest("ng")
    }
  }
}

object ManagementController
  extends ManagementController
  with MixInLinkBackupService
