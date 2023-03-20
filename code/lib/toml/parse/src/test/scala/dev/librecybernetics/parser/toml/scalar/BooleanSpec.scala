package dev.librecybernetics.parser.toml.scalar

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.scalar

class BooleanSpec extends AnyWordSpec {
  "Boolean" when {
    "Simple Valid" should {
      Map(
        "true"  -> true,
        "false" -> false
      ) foreach { (s, b) =>
        s in genericSuccess(scalar.boolean)(s, b)
      }

      Map(
        "True"  -> "must match one of the strings: {\"false\", \"true\"}",
        "False" -> "must match one of the strings: {\"false\", \"true\"}"
      ) foreach { (s, message) =>
        s in genericFailure(scalar.boolean)(s, message)
      }
    }
  }
}
