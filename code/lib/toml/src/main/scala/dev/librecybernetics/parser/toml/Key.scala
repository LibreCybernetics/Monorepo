package dev.librecybernetics.parser.toml

import scala.language.postfixOps

import cats.parse.Parser

import dev.librecybernetics.parser.*

val bareKey: Parser[String] =
  val setOfChars =
    basicLatinLetters ++
      latinDecimalDigits ++
      Set('-', '_')

  Parser.charsWhile(setOfChars contains)
