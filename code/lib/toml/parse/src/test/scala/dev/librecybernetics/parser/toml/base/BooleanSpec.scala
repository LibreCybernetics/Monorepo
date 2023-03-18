package dev.librecybernetics.parser.toml.base

import dev.librecybernetics.parser.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class BooleanSpec extends AnyWordSpec {
  "Boolean" when {
    "Simple Valid" should {
      Map(
        "true"  -> true,
        "false" -> false
      ) foreach { (s, b) =>
        s in genericSuccess(Boolean.boolean)(s, b)
      }

      Map(
        "True" -> "must match one of the strings: {\"false\", \"true\"}",
        "False" -> "must match one of the strings: {\"false\", \"true\"}"
      ) foreach { (s, message) =>
        s in genericFailure(Boolean.boolean)(s, message)
      }
    }
  }
}
