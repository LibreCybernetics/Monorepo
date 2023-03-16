package dev.librecybernetics.parser.toml.base

import scala.language.postfixOps

import cats.parse.{Parser, Parser0}

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

// Note: u000A Line Feed (LF) is \n as such it isn't included
private val disallowedChars: Set[Char] =
  ('\u0000' to '\u0008').toSet ++ ('\u000B' to '\u001F').toSet + '\u007F'

private val commentStart: Parser[Unit] =
  (hash ~ space.?).void

private val commentContent: Parser[String] =
  anyUntilNewline filter (c => !(c exists (disallowedChars contains)))

private val singleComment: Parser[String] =
  (commentStart *> commentContent <* newlineOrEnd).backtrack

/** Comment Parser Spec: https://toml.io/en/v1.0.0#comment
  *
  * A hash symbol marks the rest of the line as a comment, except when inside a string. Control characters other than
  * tab (U+0000 to U+0008, U+000A to U+001F, U+007F) are not permitted in comments.
  */
val comment: Parser[TOML.Comment] =
  singleComment.rep.map(_.toList.mkString("\n")).map(TOML.Comment.apply)
