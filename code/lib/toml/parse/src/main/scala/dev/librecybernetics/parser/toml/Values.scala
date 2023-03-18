package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.parser.rfc3339.*

lazy val scalarValues: Parser[TOML] =
  Boolean.boolean.map(TOML.Boolean.apply).backtrack |
    Float.float.map(TOML.Float.apply).backtrack |
    integer.map(TOML.Integer.apply).backtrack |
    string.map(TOML.String.apply).backtrack |
    zonedDateTime.map(TOML.ZonedDateTime.apply).backtrack |
    dateTime.map(TOML.LocalDateTime.apply).backtrack |
    date.map(TOML.LocalDate.apply).backtrack |
    time.map(TOML.LocalTime.apply).backtrack

lazy val allValues: Parser[TOML] = scalarValues | Array.array
