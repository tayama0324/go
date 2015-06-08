package domain.entity

import org.joda.time.DateTime
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
  owner: String,
)
