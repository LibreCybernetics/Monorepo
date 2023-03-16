package dev.librecybernetics.parser.toml.base

import dev.librecybernetics.parser.genericTest
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class BooleanSpec extends AnyWordSpec {
  "Boolean" when {
    "Simple Valid" should {
      Map(
        "true"  -> true,
        "false" -> false
      ) foreach { (s, b) =>
        s in genericTest(Boolean.boolean)(s, b)
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
