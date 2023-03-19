package dev.librecybernetics.parser.toml.base

import cats.implicits.*
import cats.parse.Parser

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*

class StringSpec extends AnyWordSpec {
  "String" when {
    "Valid" should {
      Map(
        "\"I'm a string. \\\"You can quote me\\\". Name\\tJos\\u00E9\\nLocation\\tSF.\""                       ->
          "I'm a string. \"You can quote me\". Name\tJos\u00E9\nLocation\tSF.",
        "\"\"\"\nRoses are red\nViolets are blue\"\"\""                                                        ->
          "Roses are red\nViolets are blue",
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
        "'''\nThe first newline is\ntrimmed in raw strings.\n   All other whitespace\n   is preserved.\n'''"   ->
          "The first newline is\ntrimmed in raw strings.\n   All other whitespace\n   is preserved.\n",
        "'''Here are fifteen quotation marks: \"\"\"\"\"\"\"\"\"\"\"\"\"\"\"'''"                               ->
          "Here are fifteen quotation marks: \"\"\"\"\"\"\"\"\"\"\"\"\"\"\"",
        "\"Here are fifteen apostrophes: '''''''''''''''\""                                                    ->
          "Here are fifteen apostrophes: '''''''''''''''",
        "''''That,' she said, 'is still pointless.''''"                                                        ->
          "'That,' she said, 'is still pointless.'",
        "\"\"\"\"This,\" she said, \"is just a pointless statement.\"\"\"\"" ->
          "\"This,\" she said, \"is just a pointless statement.\""
      ) foreach { (si, sr) =>
        si in genericSuccess(string)(si, sr)
      }
    }

    "Invalid" should {
      Map(
        "\"\\U00D80000\""   ->
          Seq(
            "must be char: '''",
            "context: multilineString, context: tripleDoubleQuote, must match string: \"\"\"\"\"",
            "context: multilineLiteral, context: tripleSingleQuote, must match string: \"'''\"",
            "must match one of the strings: {\"\\\"\", \"\\\\\", \"\\b\", \"\\f\", \"\\n\", \"\\r\", \"\\t\", \"\\u\"}",
            "must be a char within the range of: ['\u0000', '\t']",
            "must be a char within the range of: ['\u000b', '!']",
            "must be a char within the range of: ['#', '[']",
            "must be a char within the range of: [']', '￿']",
            "must fail: Invalid unicode codepoint: \\U00D80000"
          ),
        "'hello\nworld!'"   ->
          Seq(
            "must be char: '\"'",
            "context: multilineString, context: tripleDoubleQuote, must match string: \"\"\"\"\"",
            "context: multilineLiteral, context: tripleSingleQuote, must match string: \"'''\"",
            "must be char: '''"
          ),
        "\"hello\nworld!\"" ->
          Seq(
            "must be char: '''",
            "context: multilineString, context: tripleDoubleQuote, must match string: \"\"\"\"\"",
            "context: multilineLiteral, context: tripleSingleQuote, must match string: \"'''\"",
            "must be char: '\"'"
          ),
        "\"\"\"6 quotes: \"\"\"\"\"\"" ->
          Seq("must end the string"),
        "'''15 apostrophes: ''''''''''''''''''" ->
          Seq("must end the string")
      ) foreach { (s, m) =>
        s in genericFailure(string <* Parser.end)(s, m*)
      }
    }
  }
}
