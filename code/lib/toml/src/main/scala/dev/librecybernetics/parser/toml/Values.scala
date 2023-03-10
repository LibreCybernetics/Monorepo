package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.types.TOML


object Values:
  lazy val scalarValues: Parser[TOML] =
    Integer.integer.map(TOML.Integer.apply).backtrack |
      Float.float.map(TOML.Float.apply).backtrack |
      Boolean.boolean.map(TOML.Boolean.apply).backtrack

  lazy val allValues = scalarValues | Array.array