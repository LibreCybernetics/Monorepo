package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

private val simpleKey: Parser[String] =
  bareKey.backtrack | simpleLiteral.backtrack | simpleString.backtrack

private val dottedkey: Parser[NonEmptyList[String]] =
  simpleKey
    .repSep(2, dot.surroundedBy(spaces).backtrack)

private val assignment: Parser[Unit] =
  equal.surroundedBy(spaces)

val keyValue: Parser[TOML.KeyValue] =
  (
    simpleKey,
    assignment *> allValues
  ).mapN(TOML.KeyValue.apply)

// TODO: tailrec
def transformDottedToNestedMap(
    key: NonEmptyList[String],
    value: TOML
): TOML.Map =
  val NonEmptyList(headKey, tailKeys) = key
  tailKeys match
    case h :: t =>
      val nel = NonEmptyList(h, t)
      TOML.Map(
        Map(headKey -> transformDottedToNestedMap(nel, value))
      )
    case _      =>
      TOML.Map(
        Map(headKey -> value)
      )

val keyMap: Parser[TOML.Map] =
  (
    dottedkey,
    assignment *> allValues
  ).mapN { transformDottedToNestedMap }

val keyValueOrMap: Parser[TOML] =
  keyValue.backtrack | keyMap