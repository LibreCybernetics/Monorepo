package dev.librecybernetics.parser.toml.collection

import cats.Reducible
import cats.data.NonEmptyList
import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.given

private[toml] object ArrayOfTables:
  val bracketStart: Parser[Unit] =
    (bracketOpen.rep(2, 2) ~ spaces).void
  val bracketEnd: Parser[Unit]   =
    (spaces.with1 ~ bracketClose.rep(2, 2)).void

  val header: Parser[NonEmptyList[String]] =
    key
      .between(bracketStart, bracketEnd)
      .withContext("arrayOfTable.header")

  val arrayOfTables: Parser[TOML.Map] =
    (
      (header <* spaces <* comment.? <* newlineOrEnd) ~
        keyValue.repSep0(newline ~ emptyOrComment.rep0)
    ).map { (key, values) =>
      // TODO: Validate not reusing keys
      transformDottedKey(
        key,
        TOML.ArrayOfTables(
          Seq(
            values.reduceOption(_ combine _).getOrElse(TOML.Map(Map.empty))
          )
        )
      )
    }
