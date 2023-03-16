package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.semigroupTOMLMap

object Toml:
  val toml: Parser[TOML] =
    ArrayOfTables.arrayOfTables
      .repSep(newline ~ emptyLine.rep0)
      .map(nel => nel.reduce(using semigroupTOMLMap))
