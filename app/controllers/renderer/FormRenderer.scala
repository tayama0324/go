package controllers.renderer

import play.api.mvc.Result

/**
 * Renders link post form.
 */
class FormRenderer {

  def form(defaultId: String = ""): Result = {
    ???
  }
}

trait UsesFormRenderer {
  def formRenderer: FormRenderer
}

trait MixInFormRenderer {
  val formRenderer = new FormRenderer
}
