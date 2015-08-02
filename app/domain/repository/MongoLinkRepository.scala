package domain.repository

import domain.entity.Link
import domain.entity.LinkAttr
import domain.entity.LinkId
import domain.infrastructure.MixInMongoClient

/**
 * Repository for Links.
 */
abstract class MongoLinkRepository
  extends MongoRepository[LinkId, LinkAttr, Link]("go", "links")(Link.linkFormat)

trait UsesMongoLinkRepository {
  val mongoLinkRepository: MongoLinkRepository
}

trait MixInMongoLinkRepository {
  val mongoLinkRepository = new MongoLinkRepository with MixInMongoClient
}
