package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.types.TOML

val newline = Parser.char('\n')
val anyUntilNewline: Parser[String] = Parser.charsWhile(_ != '\n')

private val singleComment: Parser[String] =
  Parser.char('#') *> Parser.char(' ').backtrack.? *> // Start with `# `
    anyUntilNewline <* // content
    (newline.backtrack | Parser.end) // End marker

/** Comment Parser
 *  Spec: https://toml.io/en/v1.0.0#comment
 *
 *  A hash symbol marks the rest of the line as a comment, except when inside a
 *  string. Control characters other than tab (U+0000 to U+0008, U+000A to
 *  U+001F, U+007F) are not permitted in comments.
 */
val comment: Parser[TOML.Comment] =
  singleComment.rep.map(_.toList.mkString("\n")).map(TOML.Comment.apply)