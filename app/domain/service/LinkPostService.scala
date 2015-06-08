package domain.service

import domain.entity.Link
import domain.repository.{MixInLinkRepository, UsesLinkRepository}

/**
 * Service that creates a new link.
 */
abstract class LinkPostService extends UsesLinkRepository {

  /**
   * Insert a link.
   *
   * @param link Link.
   * @return true if succeeded, false if failed because the link is
   *         already registered.
   */
  def insert(link: Link): Boolean = {
    linkRepository.insert(link)
  }
}

trait UsesLinkPostService {
  val linkPostService: LinkPostService
}

trait MixInLinkPostService {
  val linkPostService = new LinkPostService with MixInLinkRepository
}
