package dev.librecybernetics.parser.toml

import scala.language.implicitConversions

import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.types.TOML

given Conversion[(String, String), TOML.KeyValue] with
  override def apply(t: (String, String)): TOML.KeyValue =
    val (k, v) = t
    TOML.KeyValue(k, TOML.String(v))

class KeyValueSpec extends AnyWordSpec {
  "Bay Key" when {
    "Valid Input" should {
      Map[String, TOML.KeyValue](
        "key = \"value\""                    -> ("key", "value"),
        "bare_key = \"value\""               -> ("bare_key", "value"),
        "bare-key = \"value\""               -> ("bare-key", "value"),
        "1234 = \"value\""                   -> ("1234", "value"),
        "\"127.0.0.1\" = \"value\""          -> ("127.0.0.1", "value"),
        "\"character encoding\" = \"value\"" -> ("character encoding", "value"),
        "\"ʎǝʞ\" = \"value\""                -> ("ʎǝʞ", "value"),
        "'key2' = \"value\""                 -> ("key2", "value"),
        "'quoted \"value\"' = \"value\""     -> ("quoted \"value\"", "value")
      ) foreach { (s, k) =>
        s in {
          val r = keyValue.parse(s): @unchecked
          r match
            case Left(err)    => println(show"$err"); assert(false)
            case Right("", r) => r shouldBe k
            case Right(r, _)  => println(r); assert(false)
        }
      }
    }
  }
}
