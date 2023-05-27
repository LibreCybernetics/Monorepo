package dev.librecybernetics.parser.toml

import scala.language.postfixOps

import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.scalar.*
import dev.librecybernetics.types.TOML

// TODO: tailrec
private def transformDottedKey(
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
    case Nil    =>
      TOML.Map(
        Map(headKey -> value)
      )

/** Bare keys
  *
  * ```
  * unquoted-key = 1*( ALPHA / DIGIT / %x2D / %x5F ) ; A-Z / a-z / 0-9 / - / _
  * ```
  */
private val unquotedKey: Parser[String] =
  val setOfChars: Set[Char] =
    basicLatinLetters ++
      latinDecimalDigits ++
      Set('-', '_')

  Parser.charIn(setOfChars).rep.string

/** Non-dot separated keys
  *
  * ```
  * quoted-key = basic-string / literal-string
  * simple-key = quoted-key / unquoted-key
  * ```
  */
private val simpleKey: Parser[String]               =
  (unquotedKey.backtrack | simpleLiteral.backtrack | simpleString.backtrack)
    .withContext("simple-key")

  /** Simple or Dot separated keys
    *
    * ```
    * dotted-key = simple-key 1*( dot-sep simple-key )
    * key = simple-key / dotted-key
    * ```
    */
private[toml] val key: Parser[NonEmptyList[String]] =
  simpleKey
    .repSep(dot.surroundedBy(spaces).backtrack)
    .withContext("key")
