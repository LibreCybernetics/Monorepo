package dev.librecybernetics.parser.toml

import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class StringSpec extends AnyWordSpec {
  "String" when {
    "Valid Simple" should {
      Map(
        "\"I'm a string. \\\"You can quote me\\\". Name\\tJos\\u00E9\\nLocation\\tSF.\"" ->
          "I'm a string. \"You can quote me\". Name\tJos\u00E9\nLocation\tSF."
      ) foreach { (si, sr) =>
        si in {
          val r = string.parse(si): @unchecked
          r match
            case Left(err) => println(show"$err"); assert(false)
            case Right(_, r) => r shouldBe sr
        }
      }
    }
  }
}
