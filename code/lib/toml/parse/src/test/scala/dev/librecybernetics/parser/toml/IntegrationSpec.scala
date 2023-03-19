package dev.librecybernetics.parser.toml

import java.time.{ZoneOffset, OffsetDateTime}
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
          |comments = [
          |                1,
          |                2, #this is ok
          |       ]
        """.stripMargin ->
          TOML.Map(
            Map(
              "dates"    -> TOML.Array(
                Seq(
                  OffsetDateTime.of(1987, 7, 5, 17, 45, 0, 0, ZoneOffset.UTC),
                  OffsetDateTime.of(1979, 5, 27, 7, 32, 0, 0, ZoneOffset.UTC),
                  OffsetDateTime.of(2006, 6, 1, 11, 0, 0, 0, ZoneOffset.UTC)
                )
              ),
              "comments" -> TOML.Array(Seq(1, 2))
            )
          ),
        """nest = [
          |        [
          |                ["a"],
          |                [1, 2, [3]]
          |        ]
          |       ]
        """.stripMargin ->
          TOML.Map(
            Map(
              "nest" -> TOML.Array(
                Seq(
                  TOML.Array(
                    Seq(
                      TOML.Array(Seq("a")),
                      TOML.Array(Seq(1, 2, TOML.Array(Seq(3))))
                    )
                  )
                )
              )
            )
          )
      ) foreach { (s, t) =>
        s in genericSuccess(Toml.toml)(s, t)
      }
    }
  }
}
