package domain.service

import domain.entity.LinkId
import domain.repository.MixInLinkRepository
import domain.repository.UsesLinkRepository
import domain.service.LinkDeleteService.DeleteResult

/**
 * Deletes link.
 */
abstract class LinkDeleteService extends UsesLinkRepository {

  /**
   * Deletes a link.
   *
   * It requires name of requesting user. Link will be
   * deleted if he is an owner.
   *
   * @param linkId Id of a deleting link
   * @param claimingOwner owner of the link
   * @return DeleteResult
   */
  def delete(linkId: LinkId, claimingOwner: String): DeleteResult = {
    linkRepository.find(linkId) match {
      case Some(link) if link.attr.owner == claimingOwner =>
        linkRepository.delete(linkId)
        DeleteResult.Succeeded
      case Some(_) =>
        DeleteResult.OwnerMismatch
      case None =>
        DeleteResult.NoSuchLink
    }
  }

  def forceDelete(linkId: LinkId): DeleteResult = {
    linkRepository.find(linkId) match {
      case Some(link) =>
        linkRepository.delete(linkId)
        DeleteResult.Succeeded
      case None =>
        DeleteResult.NoSuchLink
    }
  }
}

object LinkDeleteService {
  sealed trait DeleteResult
  object DeleteResult {
    case object Succeeded extends DeleteResult
    case object NoSuchLink extends DeleteResult
    case object OwnerMismatch extends DeleteResult
  }
}

trait UsesLinkDeleteService {
  def linkDeleteService: LinkDeleteService
}

trait MixInLinkDeleteService {
  val linkDeleteService: LinkDeleteService =
    new LinkDeleteService with MixInLinkRepository
}
