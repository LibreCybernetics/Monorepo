package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.semigroupTOMLMap

object Toml:
  val toml: Parser[TOML] =
    emptyOrComment.rep0.with1 *>
      (ArrayOfTables.arrayOfTables.backtrack | table.backtrack | keyValue)
        .repSep(newline ~ emptyOrComment.rep0)
        .map(nel => nel.reduce(using semigroupTOMLMap)) <*
      emptyOrComment.rep0 <* ((space | newline).rep0 ~ Parser.end)
