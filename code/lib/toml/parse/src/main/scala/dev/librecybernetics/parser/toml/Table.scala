package dev.librecybernetics.parser.toml

import cats.implicits.*
import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.given

val header: Parser[NonEmptyList[String]] =
  key
    .between(bracketOpen ~ spaces, spaces.with1 ~ bracketClose)
    .withContext("table.header")

val table: Parser[TOML.Map] =
  (
    (header <* spaces <* comment.? <* newlineOrEnd) ~
      keyValue.repSep0((newline ~ emptyOrComment.rep0).backtrack)
  ).map { (key, keyValues) =>
    transformDottedKey(
      key,
      keyValues
        .reduceOption(_ combine _)
        .getOrElse(TOML.Map(Map.empty))
    )
  }
