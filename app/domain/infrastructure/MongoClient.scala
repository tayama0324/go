package domain.infrastructure

import com.mongodb.casbah.MongoClientURI
import com.mongodb.casbah.MongoCollection

/**
 * Manages a connection to mongo.
 */
class MongoClient(uriString: String) {

  private val uri = MongoClientURI(uriString)

  private val client = {
    com.mongodb.casbah.MongoClient(uri)
  }

  /**
   * Returns mongo collection.
   *
   * @param dbNameFallback Name of DB of the collection. If not specified,
   * @param collectionNameFallback Name of the collection
   * @return MongoCollection collection
   */
  def getCollection(dbNameFallback: String, collectionNameFallback: String): MongoCollection = {
    val dbName = uri.database.getOrElse(dbNameFallback)
    val collectionName = uri.collection.getOrElse(collectionNameFallback)
    client(dbName)(collectionName)
  }
}

trait UsesMongoClient {
  val mongoClient: MongoClient
}

trait MixInMongoClient {
  val mongoClient = new MongoClient(System.getProperty("mongodb.uri"))
}
