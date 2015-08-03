package domain.repository

import com.mongodb.DBObject
import com.mongodb.DuplicateKeyException
import com.mongodb.casbah.Implicits
import com.mongodb.casbah.commons.MongoDBObject
import domain.entity.Entity
import domain.entity.EntityAttr
import domain.entity.Identity
import domain.infrastructure.UsesMongoClient
import domain.util.mongo_play_json.MongoPlayJson
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.OFormat

/**
 * Common base class for mongo repositories.
 */
abstract class MongoRepository[
  IdentityType <: Identity[String],
  AttrType <: EntityAttr,
  EntityType <: Entity[IdentityType, AttrType]
](dbName: String, collectionName: String)(
 implicit format: OFormat[EntityType]
) extends UsesMongoClient {

  protected lazy val collection = {
    val c = mongoClient.getCollection(dbName, collectionName)
    // Make entities unique by id.
    c.createIndex(MongoDBObject("id" -> 1), MongoDBObject("unique" -> true))
    c
  }

  private val emptyObject = MongoDBObject()
  private val defaultProjection = MongoDBObject(
    "entity" -> true
  )


  protected def toDbObject(entity: EntityType): DBObject = {
    Implicits.unwrapDBObj(MongoPlayJson.toMongo(format.writes(entity)))
  }

  protected def toEntity(dbObject: DBObject): EntityType = {
    Json.fromJson(MongoPlayJson.toJson(Implicits.wrapDBObj(dbObject))) match {
      case JsSuccess(entity, _) => entity
      case JsError(e) =>
        throw new IllegalArgumentException("Failed to read: " + e.mkString(", "))
    }
  }

  /**
   * Insert. Do nothing if the entity already exists.
   *
   * @param entity an entity to be inserted
   * @return true if actually inserted, false otherwise
   */
  def insert(entity: EntityType): Boolean = {
    val entry = MongoDBObject(
      "id" -> entity.id.value,
      "entity" -> toDbObject(entity)
    )
    try {
      collection.insert(entry)
      true
    } catch {
      case e: DuplicateKeyException => false
    }
  }

  /**
   * Do upsert.
   *
   * @param entity Entity to upsert
   * @return true if replaced an existing entity, false otherwise
   */
  def upsert(entity: EntityType): Boolean = {
    val entry = MongoDBObject(
      "id" -> entity.id.value,
      "entity" -> toDbObject(entity)
    )
    collection.findAndModify(
      query = MongoDBObject("id" -> entity.id.value),
      fields = emptyObject,
      sort = emptyObject,
      remove = false,
      update = entry,
      returnNew = false,
      upsert = true
    ).isDefined
  }

  def find(id: IdentityType): Option[EntityType] = {
    collection.findOne(MongoDBObject("id" -> id.value), defaultProjection)
      .map(Implicits.wrapDBObj)
      .flatMap(_.getAs[DBObject]("entity"))
      .map(toEntity)
  }

  /**
   * Removes an entity.
   * @param id Id
   * @return true if actually removed, false otherwise
   */
  def delete(id: IdentityType): Boolean = {
    collection.remove(MongoDBObject("id" -> id.value)).getN == 1
  }

  def dump(): Iterator[EntityType] = {
    collection.find(emptyObject, defaultProjection)
      .map(Implicits.wrapDBObj)
      .flatMap(_.getAs[DBObject]("entity"))
      .map(toEntity)
  }
}
