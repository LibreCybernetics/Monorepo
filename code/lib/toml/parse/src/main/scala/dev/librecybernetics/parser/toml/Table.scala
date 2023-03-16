package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML

val header: Parser[NonEmptyList[String]] =
  (dottedkey.backtrack | simpleKey.map(NonEmptyList(_, Nil)))
    .between(bracketOpen ~ spaces, spaces.with1 ~ bracketClose)

val table: Parser[TOML.Map] =
  ((header <* newlineOrEnd) ~ keyValue.repSep0(newline))
    .map { (key, keyValues) =>
      transformDottedToNestedMap(key, TOML.Array(keyValues))
    }
