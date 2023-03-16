package dev.librecybernetics.parser.toml

import scala.language.implicitConversions

import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.genericTest
import dev.librecybernetics.types.TOML

given Conversion[(String, String), TOML.Map] with
  override def apply(t: (String, String)): TOML.Map =
    val (k, v) = t
    TOML.Map(Map(k -> TOML.String(v)))

class KeyValueSpec extends AnyWordSpec {
  "Bay Key" when {
    "Valid Input" should {
      (Map[String, TOML.Map](
        "key = \"value\""                    -> ("key", "value"),
        "bare_key = \"value\""               -> ("bare_key", "value"),
        "bare-key = \"value\""               -> ("bare-key", "value"),
        "1234 = \"value\""                   -> ("1234", "value"),
        "\"127.0.0.1\" = \"value\""          -> ("127.0.0.1", "value"),
        "\"character encoding\" = \"value\"" -> ("character encoding", "value"),
        "\"ʎǝʞ\" = \"value\""                -> ("ʎǝʞ", "value"),
        "'key2' = \"value\""                 -> ("key2", "value"),
        "'quoted \"value\"' = \"value\""     -> ("quoted \"value\"", "value")
      ) ++ Map[String, TOML.Map](
        "fruit.name = \"banana\""     ->
          TOML.Map(
            Map[String, TOML.Map](
              "fruit" -> TOML.Map(
                Map("name" -> TOML.String("banana"))
              )
            )
          ),
        "fruit. color = \"yellow\""   ->
          TOML.Map(
            Map[String, TOML.Map](
              "fruit" -> TOML.Map(
                Map("color" -> TOML.String("yellow"))
              )
            )
          ),
        "fruit . flavor = \"banana\"" ->
          TOML.Map(
            Map[String, TOML.Map](
              "fruit" -> TOML.Map(
                Map("flavor" -> TOML.String("banana"))
              )
            )
          )
      )) foreach { (s, k) =>
        s in genericTest(keyValueOrMap)(s, k)
      }
    }
  }
}
