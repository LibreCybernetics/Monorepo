package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.semigroupTOMLMap

val emptyOrComment: Parser[Unit] =
  (emptyLine | (spaces.with1 *> comment <* newline).void).backtrack

object Toml:
  val toml: Parser[TOML] =
    emptyOrComment.rep0.with1 *>
      (ArrayOfTables.arrayOfTables | table | keyValueOrMap)
        .repSep(newline ~ emptyOrComment.rep0)
        .map(nel => nel.reduce(using semigroupTOMLMap)) <*
      emptyOrComment.rep0 <* Parser.end
