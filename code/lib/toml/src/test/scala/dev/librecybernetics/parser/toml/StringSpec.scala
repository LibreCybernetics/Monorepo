package dev.librecybernetics.parser.toml

import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class StringSpec extends AnyWordSpec {
  "String" when {
    "Valid Simple" should {
      Map(
        "\"I'm a string. \\\"You can quote me\\\". Name\\tJos\\u00E9\\nLocation\\tSF.\""                       ->
          "I'm a string. \"You can quote me\". Name\tJos\u00E9\nLocation\tSF.",
        "\"\"\"\nRoses are red\nViolets are blue\"\"\""                                                        -> "Roses are red\nViolets are blue",
        "\"\"\"\nThe quick brown \\\n\n\n  fox jumps over \\\n    the lazy dog.\"\"\""                         ->
          "The quick brown fox jumps over the lazy dog.",
        "\"\"\"\\\n       The quick brown \\\n       fox jumps over \\\n       the lazy dog.\\\n       \"\"\"" ->
          "The quick brown fox jumps over the lazy dog.",
        "\"\"\"Here are two quotation marks: \"\". Simple enough.\"\"\""                                       ->
          "Here are two quotation marks: \"\". Simple enough.",
        "\"\"\"Here are three quotation marks: \"\"\\\".\"\"\"" ->
          "Here are three quotation marks: \"\"\".",
        "\"\"\"\"This,\" she said, \"is just a pointless statement.\"\"\"\"" ->
          "\"This,\" she said, \"is just a pointless statement.\""
      ) foreach { (si, sr) =>
        si in {
          val r = string.parse(si): @unchecked
          r match
            case Left(err)   => println(show"$err"); assert(false)
            case Right(_, r) => r shouldBe sr
        }
      }
    }
  }
}
