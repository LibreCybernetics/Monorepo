package dev.librecybernetics.parser.toml

import scala.language.implicitConversions

import cats.parse.Parser
import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.genericTest
import dev.librecybernetics.types.TOML

given Conversion[Int, TOML] with
  def apply(n: Int): TOML = TOML.Integer(BigInt(n))

given Conversion[Double, TOML] with
  def apply(n: Double): TOML = TOML.Float(n)

given Conversion[Boolean, TOML] with
  def apply(n: Boolean): TOML = TOML.Boolean(n)

given Conversion[Seq[TOML], TOML] with
  def apply(n: Seq[TOML]): TOML = TOML.Array(n)

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
//        "[ 0.1, 0.2, 0.5, 1, 2, 5 ]" ->
//          TOML.Array(Seq(
//            0.1d,
//            0.2d,
//            0.5d,
//            1,
//            2,
//            5
//          )),
        "[1,2,3,]"                              ->
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
