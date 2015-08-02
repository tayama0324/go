package domain.infrastructure

import com.mongodb.ServerAddress
import com.mongodb.casbah.MongoClientOptions
import com.mongodb.casbah.MongoCollection

/**
 * Manages a connection to mongo.
 */
class MongoClient(host: String, port: Int) {

  private val client = {
    println("initializing...")
    val options = MongoClientOptions()
    val serverAddress = new ServerAddress(host, port)
    println("connecting " + host + " " + port)
    com.mongodb.casbah.MongoClient(serverAddress, options)
  }

  /**
   * Returns mongo collection.
   *
   * @param dbName Name of DB of the collection
   * @param collectionName Name of the collection
   * @return MongoCollection collection
   */
  def getCollection(dbName: String, collectionName: String): MongoCollection = {
    try {
      client(dbName)(collectionName)
    } catch {
      case e: Throwable => e.printStackTrace()
        throw e
    }
  }
}

trait UsesMongoClient {
  val mongoClient: MongoClient
}

trait MixInMongoClient {
  val mongoClient = new MongoClient("localhost", 27017)
}
