package dev.librecybernetics.parser.toml.base

import cats.implicits.*
import dev.librecybernetics.parser.genericTest
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
        "\"\"\"Here are three quotation marks: \"\"\\\".\"\"\""                                                ->
          "Here are three quotation marks: \"\"\".",
        "\"\"\"\"This,\" she said, \"is just a pointless statement.\"\"\"\""                                   ->
          "\"This,\" she said, \"is just a pointless statement.\"",
        "'C:\\Users\\nodejs\\templates'"                                                                       ->
          "C:\\Users\\nodejs\\templates",
        "'\\\\ServerX\\admin$\\system32\\'"                                                                    ->
          "\\\\ServerX\\admin$\\system32\\",
        "'Tom \"Dubs\" Preston-Werner'"                                                                        ->
          "Tom \"Dubs\" Preston-Werner",
        "'<\\i\\c*\\s*>'"                                                                                      ->
          "<\\i\\c*\\s*>",
        "'''I [dw]on't need \\d{2} apples'''"                                                                  ->
          "I [dw]on't need \\d{2} apples",
        "'''\nThe first newline is\ntrimmed in raw strings.\n   All other whitespace\n   is preserved.\n'''" ->
          "The first newline is\ntrimmed in raw strings.\n   All other whitespace\n   is preserved.\n",
        "'''Here are fifteen quotation marks: \"\"\"\"\"\"\"\"\"\"\"\"\"\"\"'''" ->
          "Here are fifteen quotation marks: \"\"\"\"\"\"\"\"\"\"\"\"\"\"\"",
        "\"Here are fifteen apostrophes: '''''''''''''''\"" ->
          "Here are fifteen apostrophes: '''''''''''''''",
        "''''That,' she said, 'is still pointless.''''" ->
          "'That,' she said, 'is still pointless.'"

      ) foreach { (si, sr) =>
        si in genericTest(string)(si, sr)
      }
    }
  }
}
