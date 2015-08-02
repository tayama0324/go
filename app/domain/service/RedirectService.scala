package domain.service

import domain.entity.{Link, LinkId}
import domain.repository.MixInMongoLinkRepository
import domain.repository.UsesMongoLinkRepository
import domain.repository.{MixInLinkRepository, UsesLinkRepository}
import spray.http.Uri
import spray.http.Uri.Path.Segment

import scalaz.{-\/, \/-, \/}

/**
 * Resolves redirect destination.
 */
abstract class RedirectService
  extends UsesMongoLinkRepository {

  /**
   * Resolve redirect destination.
   *
   * TODO: Define concrete redirect rule.
   *
   * @param path requested path
   * @return \/-(Uri) URL if successfully resolved.
   *         -\/(NoSuchElementException) if undefined links
   *         -\/(IllegalArgumentException) if invalid requested path.
   */
  def resolve(path: Uri.Path): Throwable \/ Uri = {
    path match {
      case Segment(head, tail) =>
        val uriOpt = for {
          id <- LinkId.of(head)
          link <- mongoLinkRepository.find(id)
        } yield buildUrl(link, tail)

        uriOpt match {
          case Some(u) => \/-(u)
          case None => -\/(new NoSuchElementException("Undefined link: " + head))
        }
      case _ => -\/(new IllegalArgumentException(
        "path have to start with a non-slash letter."
      ))
    }
  }

  private def buildUrl(link: Link, rest: Uri.Path): Uri = {
    val newPath = link.attr.destination.path ++ rest
    link.attr.destination.withPath(newPath)
  }
}

trait UsesRedirectService {
  val redirectService: RedirectService
}

trait MixInRedirectService {
  val redirectService = new RedirectService with MixInMongoLinkRepository
}
