package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.semigroupTOMLMap

object Toml:
  val toml: Parser[TOML] =
    (emptyLine | (comment ~ newline)).rep0.with1 *>
      (ArrayOfTables.arrayOfTables | table | keyValueOrMap)
        .repSep(newline ~ (emptyLine | (comment ~ newline)).rep0)
        .map(nel => nel.reduce(using semigroupTOMLMap))
