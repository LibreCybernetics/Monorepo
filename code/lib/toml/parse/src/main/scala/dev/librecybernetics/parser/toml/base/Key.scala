package dev.librecybernetics.parser.toml.base

import cats.data.NonEmptyList
import scala.language.postfixOps

import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

// TODO: tailrec
def transformDottedKey(
    key: NonEmptyList[String],
    value: TOML
): TOML.Map =
  val NonEmptyList(headKey, tailKeys) = key
  tailKeys match
    case h :: t =>
      val nel = NonEmptyList(h, t)
      TOML.Map(
        Map(headKey -> transformDottedKey(nel, value))
      )
    case _      =>
      TOML.Map(
        Map(headKey -> value)
      )
  end match

private val bareKey: Parser[String] =
  val setOfChars =
    basicLatinLetters ++
      latinDecimalDigits ++
      Set('-', '_')

  Parser.charIn(setOfChars).rep.string

// Non-dot separated keys
private val simpleKey: Parser[String] =
  (bareKey.backtrack | simpleLiteral.backtrack | simpleString.backtrack)
    .withContext("simple-key")

// Simple or Dot separated keys
val key: Parser[NonEmptyList[String]] =
  simpleKey
    .repSep(dot.surroundedBy(spaces).backtrack)
    .withContext("key")
