package domain.repository

import domain.entity.{LinkAttr, LinkId, Link}

import scala.collection.concurrent.TrieMap

/**
 * Repository for Links.
 *
 * This implementation stores entities on memory. Be aware that
 * entities are not persisted.
 */
class LinkRepository() {

  private val values: TrieMap[LinkId, LinkAttr] = new TrieMap

  /**
   * Store a Link. Do nothing if there exists already.
   *
   * @param link Link.
   * @return true if the link would be actually inserted, false otherwise.
   */
  def insert(link: Link): Boolean = {
    values.putIfAbsent(link.id, link.attr).isEmpty
  }

  /**
   * Store a Link. Overwrites if there exists already.
   *
   * @param link Link.
   */
  def upsert(link: Link): Unit = {
    values.put(link.id, link.attr)
  }

  /**
   * Retrieves a link.
   *
   * @param linkId LinkId.
   * @return Some(Link) if found
   *         None otherwise
   */
  def find(linkId: LinkId): Option[Link] = {
    values.get(linkId).map(Link(linkId, _))
  }

  /**
   * Removes a link.
   *
   * @param linkId LinkId.
   */
  def delete(linkId: LinkId): Unit = {
    values.remove(linkId)
  }

  /**
   * Returns all links stored on this repository.
   *
   * @return Seq of links.
   */
  def dump(): Seq[Link] = {
    values.map { case (id, attr) => Link(id, attr) }.toSeq
  }
}

object LinkRepository extends LinkRepository

trait UsesLinkRepository {
  val linkRepository: LinkRepository
}

trait MixInLinkRepository {
  val linkRepository = LinkRepository
}
