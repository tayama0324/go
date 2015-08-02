package domain.entity

import play.api.data.validation.ValidationError
import play.api.libs.json.Format
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import scala.util.Success
import scala.util.Try
import spray.http.Uri

/**
 * An entity that represents a single short link settings.
 */
case class Link(id: LinkId, attr: LinkAttr) extends Entity[LinkId, LinkAttr]

case class LinkId private(value: String) extends Identity[String]

object LinkId {

  /**
   * Construct a LinkId from a String.
   *
   * Note that only alphanumerics, and some punctuation marks are
   * allowed for LinkId.
   *
   * TODO: Reject invalid strings.
   *
   * @param value a String
   * @return Some(LinkId) if succeeded
   *         None if a value contains unacceptable letters.
   */
  def of(value: String): Option[LinkId] = Some(LinkId(value))
}

case class LinkAttr(
  destination: Uri,
  owner: String
) extends EntityAttr

object Link {

  private val urlReads = Reads.StringReads
    .map(s => Try(Uri(s)))
    .collect(ValidationError("Invalid url.")) { case Success(u) => u }

  private val urlWrites = Writes[Uri](u => JsString(u.toString()))

  private implicit val urlFormat = Format(urlReads, urlWrites)

  implicit val linkIdReads = Reads.StringReads
    .map(LinkId.of)
    .collect(ValidationError("Invalid link id.")) { case Some(id) => id }

  implicit val linkIdWrites = Writes[LinkId](id => JsString(id.value))

  implicit val linkIdFormat = Format(linkIdReads, linkIdWrites)

  implicit val linkAttrFormat = Json.format[LinkAttr]

  implicit val linkFormat = Json.format[Link]
}