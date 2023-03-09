package dev.librecybernetics.parser.toml

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class FloatSpec extends AnyWordSpec {
  "Float" when {
    "Valid Simple Float" should {
      Map(
        "+1.0" -> 1.0d,
        "3.1415" -> 3.1415d,
        "-0.01" -> -0.01d
      ) foreach { (s, d) =>
        s in {
          val Right("", r) = Float.tomlFloat.parse(s): @unchecked
          r shouldBe d
        }
      }
    }
  }
}
