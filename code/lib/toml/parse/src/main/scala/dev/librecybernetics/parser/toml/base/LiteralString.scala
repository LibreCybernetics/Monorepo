package dev.librecybernetics.parser.toml.base

import cats.parse.Parser

import dev.librecybernetics.parser.*

private val singleQuote = Parser.char('\'')
private val tripleSingleQuote = Parser.string("'''").withContext("tripleSingleQuote")

val simpleLiteral: Parser[String] =
  Parser
    .charsWhile(c => !(Set('\'', '\n') contains c))
    .surroundedBy(singleQuote)

val multilineLiteral: Parser[String] =
  (
    newline.?.with1 *> (
      (Parser.not(tripleSingleQuote).with1 *> Parser.anyChar).rep.string |
        (Parser.char('\'').string <* Parser.peek(tripleSingleQuote <* Parser.not(singleQuote))).backtrack
      ).withContext("multilineLiteral.line")
      .rep
      .map(_.toList.mkString(""))
    ).surroundedBy(tripleSingleQuote).withContext("multilineLiteral")
