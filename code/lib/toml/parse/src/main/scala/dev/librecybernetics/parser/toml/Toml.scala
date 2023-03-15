package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.types.TOML

object Toml:
  val toml: Parser[TOML] =
    ArrayOfTables.arrayOfTables
