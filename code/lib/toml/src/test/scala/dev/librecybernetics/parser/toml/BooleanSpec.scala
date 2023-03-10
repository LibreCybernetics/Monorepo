package dev.librecybernetics.parser.toml

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class BooleanSpec extends AnyWordSpec {
  "Boolean" when {
    "Simple Valid" should {
      Map(
        "true" -> true,
        "false" -> false
      ) foreach { (s, b) =>
        s in {
          val Right(("", r)) = Boolean.boolean.parse(s) : @unchecked
          r shouldBe b
        }
      }

      Set(
        "True",
        "False"
      ) foreach { s =>
        s in {
          val Left(err) = Boolean.boolean.parse(s): @unchecked
          err.input foreach (_ shouldBe s)
        }
      }
    }
  }
}
