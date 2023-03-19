package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML

val header: Parser[NonEmptyList[String]] =
  key
    .between(bracketOpen ~ spaces, spaces.with1 ~ bracketClose)
    .withContext("table.header")

val table: Parser[TOML.Map] =
  (
    (header <* spaces <* comment.? <* newlineOrEnd) ~
      keyValue.repSep0(newline ~ emptyOrComment.rep0)
  ).map { (key, keyValues) =>
    transformDottedKey(key, TOML.Array(keyValues))
  }
