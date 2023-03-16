package dev.librecybernetics.parser.toml

import cats.Reducible
import cats.implicits.*
import cats.data.NonEmptyList
import cats.parse.Parser
import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.semigroupTOMLMap

object ArrayOfTables:
  val bracketStart: Parser[Unit] =
    (bracketOpen.rep(2, 2) ~ spaces).void
  val bracketEnd: Parser[Unit]   =
    (spaces.with1 ~ bracketClose.rep(2, 2)).void

  val header: Parser[NonEmptyList[String]] =
    bracketStart *>
      (dottedkey.backtrack | simpleKey.map(NonEmptyList.one)).withContext("arrayOfTable.header.key") <*
      bracketEnd

  val arrayOfTables: Parser[TOML.Map] =
    ((header.withContext("arrayOfTable.header") <* newlineOrEnd) ~
      keyValue.repSep0((newline ~ emptyLine.rep0).void | Parser.end)).map { (key, values) =>
      // TODO: Validate not reusing keys
      transformDottedToNestedMap(
        key,
        TOML.Array(Seq(
          values.reduceOption(_ combine _).getOrElse(TOML.Map(Map.empty))
        ))
      )
    }
