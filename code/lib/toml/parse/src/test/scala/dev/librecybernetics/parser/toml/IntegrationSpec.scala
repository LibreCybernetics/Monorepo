package dev.librecybernetics.parser.toml

import java.time.{ZoneOffset, ZonedDateTime}
import scala.language.implicitConversions

import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.given

class IntegrationSpec extends AnyWordSpec {
  "TOML" when {
    "Valid" should {
      Map(
        // Test case from toml-test: https://github.com/BurntSushi/toml-test
        """dates = [
          |  1987-07-05T17:45:00Z,
          |  1979-05-27T07:32:00Z,
          |  2006-06-01T11:00:00Z,
          |]
        """.stripMargin ->
          TOML.Map(
            Map(
              "dates" -> TOML.Array(
                Seq(
                  ZonedDateTime.of(1987, 7, 5, 17, 45, 0, 0, ZoneOffset.UTC),
                  ZonedDateTime.of(1979, 5, 27, 7, 32, 0, 0, ZoneOffset.UTC),
                  ZonedDateTime.of(2006, 6, 1, 11, 0, 0, 0, ZoneOffset.UTC)
                )
              )
            )
          ),
        """comments = [
          |                1,
          |                2, #this is ok
          |       ]
        """.stripMargin ->
          TOML.Map(Map("comments" -> TOML.Array(Seq(1, 2))))
      ) foreach { (s, t) =>
        s in genericSuccess(Toml.toml)(s, t)
      }
    }
  }
}
