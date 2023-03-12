package dev.librecybernetics.parser.toml

import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.types.TOML

class TableSpec extends AnyWordSpec {
  "Table" when {
    "Valid Cases" should {
      Map(
        "[table]" -> TOML.Map(
          Map("table" -> TOML.Array(Nil))
        )
      ) foreach { (s, t) =>
        s in {
          val r = table.parse(s): @unchecked
          r match
            case Left(err)    => println(show"$err"); assert(false)
            case Right("", r) => r shouldBe t
            case Right(m, _)  => println(show"missing: $m"); assert(false)
        }
      }
    }
  }
}
