package dev.librecybernetics.parser.toml

import dev.librecybernetics.types.TOML

import cats.parse.Parser
import cats.implicits.*
import scala.language.implicitConversions
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

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
        "[ [ 1, 2 ], [3, 4, 5] ]" ->
          TOML.Array(Seq(
            TOML.Array(Seq(1, 2)),
            TOML.Array(Seq(3, 4, 5))
          )),
        "[ [ 1, 2 ], [true,   false, true  ] ]" ->
          TOML.Array(Seq(
            TOML.Array(Seq(1, 2)),
            TOML.Array(Seq(true, false, true))
          )),
//        "[ 0.1, 0.2, 0.5, 1, 2, 5 ]" ->
//          TOML.Array(Seq(
//            0.1d,
//            0.2d,
//            0.5d,
//            1,
//            2,
//            5
//          ))
      ) foreach { (s, a) =>
        s in {
          val r = Array.array.parse(s): @unchecked
          r match
            case Right("", r) => r shouldBe a
            case Right(_, _) => assert(false)
            case Left(err) =>
              println(show"$err")
              assert(false)
        }
      }
    }
  }
}
