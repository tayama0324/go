package domain.service

import domain.entity.Link
import domain.repository.MixInMongoLinkRepository
import domain.repository.UsesMongoLinkRepository
import domain.repository.{MixInLinkRepository, UsesLinkRepository}
import scalaz.-\/
import scalaz.\/
import scalaz.\/-

/**
 * Service that creates a new link.
 */
abstract class LinkPostService extends UsesMongoLinkRepository {

  /**
   * Insert a link.
   *
   * @param link Link.
   * @return \/-() if succeeded
   *         -\/(AlreadyRegistered) if so
   *         -\/(Reserved) if specified id is reserved for managing
   *           purpose and users are not allowed to use
   */
  def insert(link: Link): LinkPostService.Error \/ Unit = {
    if (LinkPostService.blackListedPaths.contains(link.id.value)) {
      -\/(LinkPostService.Error.Reserved)
    } else {
      if (mongoLinkRepository.insert(link)) {
        \/-()
      } else {
        -\/(LinkPostService.Error.AlreadyRegistered)
      }
    }
  }
}

object LinkPostService {
  sealed trait Error
  object Error {
    case object AlreadyRegistered extends Error
    case object Reserved extends Error
  }

  val blackListedPaths = Seq("go-link-management", "assets")
}

trait UsesLinkPostService {
  val linkPostService: LinkPostService
}

trait MixInLinkPostService {
  val linkPostService = new LinkPostService with MixInMongoLinkRepository
}
