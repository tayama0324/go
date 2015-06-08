package domain.service

import domain.entity.Link
import domain.repository.{MixInLinkRepository, UsesLinkRepository}

/**
 * Allows dump and restore link repository.
 */
abstract class LinkBackupService extends UsesLinkRepository {

  /**
   * Extract all links in repository.
   *
   * @return Seq of links.
   */
  def dump(): Seq[Link] = linkRepository.dump()

  /**
   * Insert given link into into repository.
   *
   * If repository already has a given link, it will be replaced with
   * the given link.
   * @param links Links to be inserted.
   */
  def restore(links: Seq[Link]): Unit = links.foreach(linkRepository.upsert)
}

trait UsesLinkBackupService {
  val linkBackupService: LinkBackupService
}

trait MixInLinkBackupService {
  val linkBackupService = new LinkBackupService with MixInLinkRepository
}
