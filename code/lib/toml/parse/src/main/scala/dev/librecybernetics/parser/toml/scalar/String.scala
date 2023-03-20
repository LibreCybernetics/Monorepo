package dev.librecybernetics.parser.toml.scalar

import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.util.*

private val doubleQuote = Parser.char('"')
private val tripleDoubleQuote = Parser.string("\"\"\"").withContext("tripleDoubleQuote")

private[toml] val simpleString: Parser[String] =
  (
    Parser.charsWhile(c => !(Set('"', '\\', '\n') contains c)).backtrack |
      escaped.backtrack | escapedUnicode4.backtrack | escapedUnicode8.backtrack
  ).rep
    .map(_.toList.mkString(""))
    .surroundedBy(doubleQuote)

private[toml] val multilineString: Parser[String] =
  (
    newline.?.with1 *> (
      (Parser.not(tripleDoubleQuote | backslash).with1 *> Parser.anyChar).rep.string |
        escaped.backtrack | escapedUnicode4.backtrack | escapedUnicode8.backtrack | escapedNewline.backtrack |
        (Parser.char('\"').string <* Parser.peek(tripleDoubleQuote <* Parser.not(doubleQuote))).backtrack
    )
      .withContext("multilineString.line")
      .rep
      .map(_.toList.mkString(""))
  )
    .surroundedBy(tripleDoubleQuote)
    .withContext("multilineString")

private[toml] val string: Parser[String] =
  // Note: "" is a valid simple string, check triple quote first
  (
    multilineLiteral.backtrack | simpleLiteral.backtrack |
      multilineString.backtrack | simpleString.backtrack
  ).checkDisallowedChars
