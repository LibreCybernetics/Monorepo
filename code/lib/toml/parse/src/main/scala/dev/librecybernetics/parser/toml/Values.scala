package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML

lazy val scalarValues: Parser[TOML] =
  string.map(TOML.String.apply).backtrack |
  integer.map(TOML.Integer.apply).backtrack |
    Float.float.map(TOML.Float.apply).backtrack |
    Boolean.boolean.map(TOML.Boolean.apply).backtrack

lazy val allValues: Parser[TOML] = scalarValues | Array.array