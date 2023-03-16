package dev.librecybernetics.parser.toml.base

import cats.data.NonEmptyList

import scala.language.postfixOps
import cats.parse.Parser
import dev.librecybernetics.parser.*

private val bareKey: Parser[String] =
  val setOfChars =
    basicLatinLetters ++
      latinDecimalDigits ++
      Set('-', '_')

  Parser.charsWhile(setOfChars contains)

val simpleKey: Parser[String] =
  bareKey.backtrack | simpleLiteral.backtrack | simpleString.backtrack

val dottedkey: Parser[NonEmptyList[String]] =
  simpleKey
    .repSep(2, dot.surroundedBy(spaces).backtrack)
