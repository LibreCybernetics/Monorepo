package dev.librecybernetics.parser.toml

import scala.language.implicitConversions

import cats.implicits.*
import cats.parse.Parser
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.genericSuccess
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.given

class ArraySpec extends AnyWordSpec {
  "Array" when {
    "Valid" should {
      Map(
        "[]"                                    ->
          TOML.Array(Nil),
        "[[]]"                                  ->
          TOML.Array(Seq(TOML.Array(Nil))),
        "[1]"                                   ->
          TOML.Array(Seq(1)),
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
            Seq(0.1d, 0.2d, 0.5d, 1, 2, 5)
          ),
        "[1, 2, 3, ]"                           ->
          TOML.Array(Seq(1, 2, 3)),
        // Test case from toml-test: https://github.com/BurntSushi/toml-test
        "[1, [\"Arrays are not integers.\"]]" ->
          TOML.Array(Seq(
            1, TOML.Array(Seq("Arrays are not integers."))
          ))
      ) foreach { (s, a) =>
        s in genericSuccess(Array.array)(s, a)
      }
    }
  }
}
