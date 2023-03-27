package dev.librecybernetics.parser.toml.collection

import scala.language.implicitConversions

import cats.implicits.*
import cats.parse.Parser
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.genericSuccess
import dev.librecybernetics.parser.toml.collection
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.given

class ArraySpec extends AnyWordSpec {
  "ScalarArray" when {
    "Valid" should {
      Map(
        "[]"                                    ->
          TOML.ScalarArray(Nil),
        "[[]]"                                  ->
          TOML.ScalarArray(Seq(TOML.ScalarArray(Nil))),
        "[1]"                                   ->
          TOML.ScalarArray(Seq(1)),
        "[ [ 1, 2 ], [3, 4, 5] ]"               ->
          TOML.ScalarArray(
            Seq(
              TOML.ScalarArray(Seq(1, 2)),
              TOML.ScalarArray(Seq(3, 4, 5))
            )
          ),
        "[ [ 1, 2 ], [true,   false, true  ] ]" ->
          TOML.ScalarArray(
            Seq(
              TOML.ScalarArray(Seq(1, 2)),
              TOML.ScalarArray(Seq(true, false, true))
            )
          ),
        "[ 0.1, 0.2, 0.5, 1, 2, 5 ]"            ->
          TOML.ScalarArray(
            Seq(0.1d, 0.2d, 0.5d, 1, 2, 5)
          ),
        "[1, 2, 3, ]"                           ->
          TOML.ScalarArray(Seq(1, 2, 3)),
        // Test case from toml-test: https://github.com/BurntSushi/toml-test
        "[1, [\"Arrays are not integers.\"]]" ->
          TOML.ScalarArray(Seq(
            1, TOML.ScalarArray(Seq("Arrays are not integers."))
          ))
      ) foreach { (s, a) =>
        s in genericSuccess(collection.Array.array)(s, a)
      }
    }
  }
}
