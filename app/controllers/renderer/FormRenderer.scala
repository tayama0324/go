package controllers.renderer

import play.api.mvc.{Results, Result}

/**
 * Renders link post form.
 */
class FormRenderer {

  def form(defaultId: String = ""): Result = {
    Results.Ok(views.html.form(defaultId, None, None))
  }

  def onRegistered(id: String): Result = {
    Results.Ok(views.html.form("", Some(id + "を登録しました。"), None))
  }

  def onDeleted(id: String): Result = {
    Results.Ok(views.html.form("", Some(id + "を削除しました。"), None))
  }

  def onError(reason: Option[String] = None): Result = {
    Results.Ok(views.html.form("", None, Some("エラー" + reason.map(": " + _).getOrElse(""))))
  }
}

trait UsesFormRenderer {
  def formRenderer: FormRenderer
}

trait MixInFormRenderer {
  val formRenderer = new FormRenderer
}
