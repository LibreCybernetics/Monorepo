package dev.librecybernetics.parser.toml

import scala.language.implicitConversions

import cats.implicits.*
import cats.parse.Parser
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.genericTest
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.given

class ArraySpec extends AnyWordSpec {
  "Array" when {
    "Valid" should {
      Map(
        "[ [ 1, 2 ], [3, 4, 5] ]"               ->
          TOML.Array(
            Seq(
              TOML.Array(Seq(1, 2)),
              TOML.Array(Seq(3, 4, 5))
            )
          ),
        "[ [ 1, 2 ], [true,   false, true  ] ]" ->
          TOML.Array(
            Seq(
              TOML.Array(Seq(1, 2)),
              TOML.Array(Seq(true, false, true))
            )
          ),
        "[ 0.1, 0.2, 0.5, 1, 2, 5 ]"            ->
          TOML.Array(
            Seq(
              0.1d, 0.2d, 0.5d, 1, 2, 5
            )
          ),
        "[1, 2, 3, ]"                              ->
          TOML.Array(
            Seq(
              TOML.Integer(1),
              TOML.Integer(2),
              TOML.Integer(3)
            )
          )
      ) foreach { (s, a) =>
        s in genericTest(Array.array)(s, a)
      }
    }
  }
}
