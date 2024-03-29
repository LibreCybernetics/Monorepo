package dev.librecybernetics.parser.toml

import scala.language.postfixOps

import cats.parse.{Parser, Parser0}

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.util.*
import dev.librecybernetics.types.TOML

private val commentStart: Parser[Unit] =
  (hash ~ whitespace.rep0).void

private val commentContent: Parser[String] =
  anyUntilNewline.checkDisallowedChars

private val singleComment: Parser[String] =
  (commentStart *> commentContent <* Parser.peek(newlineOrEnd)).backtrack

/** Comment Parser Spec: https://toml.io/en/v1.0.0#comment
  *
  * A hash symbol marks the rest of the line as a comment, except when inside a string. Control characters other than
  * tab (U+0000 to U+0008, U+000A to U+001F, U+007F) are not permitted in comments.
  *
  * ```
  * comment-start-symbol = %x23 ; #
  * non-ascii = %x80-D7FF / %xE000-10FFFF
  * non-eol = %x09 / %x20-7F / non-ascii
  *
  * comment = comment-start-symbol *non-eol
  * ```
  */
private[toml] val comment: Parser[String] =
  singleComment
    .repSep(newline)
    .map(_.toList.mkString("\n"))
