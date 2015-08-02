package domain.util.mongo_play_json

import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.mongodb.casbah.commons.Implicits
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNull
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import scala.util.Try
import scala.util.control.NonFatal

/**
 * Converts casbah MongoDBObject to play-json JsObject, and vice versa.
 */
object MongoPlayJson {

  private object MongoToJson {

    private def convert(any: Any): JsValue = {
      any match {
        case null => JsNull
        case b: Boolean => JsBoolean(b)
        case n: Int => JsNumber(n)
        case n: Long => JsNumber(n)
        case n: Float => JsNumber(n.toDouble)
        case n: Double => JsNumber(n)
        case n: BigInt => JsNumber(BigDecimal(n))
        case n: BigDecimal => JsNumber(n)
        case s: String => JsString(s)
        case m: DBObject => convertMap(Implicits.wrapDBObj(m))
        case m: collection.Map[_, _] => convertMap(m)
        case a: Iterable[_] => convertArray(a)
        case e => throw new IllegalArgumentException(
          s"Can't jsonize the element `$e` of class ${e.getClass.getCanonicalName}"
        )
      }
    }

    private def convertArray(array: Iterable[_]): JsArray = {
      JsArray(array.map(convert).toSeq)
    }

    private def convertMap(map: collection.Map[_, _]): JsObject = {
      val elements = map.map {
        case (key: String, value) =>
          key -> convert(value)
        case (key, _) =>
          throw new IllegalArgumentException("Map key must be a string: " + key)
      }
      JsObject(elements.toSeq)
    }

    def toJson(mongo: MongoDBObject): JsObject = {
      convertMap(mongo)
    }
  }

  private object JsonToMongo {

    private def convertJsValue(jsValue: JsValue): Any = {
      jsValue match {
        case JsNull => null
        case JsBoolean(b) => b
        case JsNumber(n) => convertBigDecimal(n)
        case JsString(s) => s
        case a: JsArray => convertJsArray(a)
        case o: JsObject => convertJsObject(o)
        case e => throw new IllegalArgumentException("Can't convert to mongo object: " + e)
      }
    }

    private def convertBigDecimal(n: BigDecimal): Any = {
      Try(n.toIntExact) orElse Try(n.toLongExact) getOrElse n.toDouble
    }

    private def convertJsArray(jsArray: JsArray): Any = {
      jsArray.value.map(convertJsValue)
    }

    private def convertJsObject(jsObject: JsObject): MongoDBObject = {
      val elements = jsObject.fields.map {
        case (key, value) => key -> convertJsValue(value)
      }
      try {
        Implicits.wrapDBObj(MongoDBObject(elements: _*))
      } catch {
        case NonFatal(e) => throw new IllegalArgumentException("Can't serialize.", e)
      }
    }
    def toMongo(jsObject: JsObject): MongoDBObject = {
      convertJsObject(jsObject)
    }
  }

  /**
   * Converts casbah MongoDBObject to play-json JsObject.
   *
   * Boolean is converted into JsBoolean String is converted into JsString,
   * Int, Long, Float, Double, BigInt, BigDecimal is converted into JsNumber,
   * null is converted into JsNull, Map and Iterable are converted into
   * JsObject and JsArray, respectively.
   *
   * @param mongo MongoDBObject
   * @return play-json JsObject
   * @throws IllegalArgumentException if given object contains an element
   *             of inconvertible type, or a map where type of key is
   *             not string
   */
  def toJson(mongo: MongoDBObject): JsObject = MongoToJson.toJson(mongo)

  /**
   * Converts play-json JsObject into casbah MongoDBObject.
   *
   * JsBoolean is converted into Boolean, JsString is converted into String,
   * JsNumber is converted into Int or Long or Double if representable,
   * JsNull is converted into null, respectively.
   * JsObject and JsArray are converted recursively.
   *
   * @param jsObject play-json JsObject
   * @return casbah MongoDBObject
   */
  def toMongo(jsObject: JsObject): MongoDBObject = JsonToMongo.toMongo(jsObject)
}
