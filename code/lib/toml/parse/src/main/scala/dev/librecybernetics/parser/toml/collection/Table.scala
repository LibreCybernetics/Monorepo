package dev.librecybernetics.parser.toml.collection

import cats.implicits.*
import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.parser.toml.*
import dev.librecybernetics.parser.toml.util.*
import dev.librecybernetics.types.toml.given

private val header: Parser[NonEmptyList[String]] =
  key
    .between(bracketOpen ~ spaces, spaces.with1 ~ bracketClose)
    .withContext("table.header")

private[toml] val table: Parser[TOML.Map] =
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
