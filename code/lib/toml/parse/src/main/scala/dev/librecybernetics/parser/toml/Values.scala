package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.rfc3339.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML

// These all start with digit so order is important
private val integerStartValues: Parser[TOML] =
  Parser.peek(digit).with1 *>
    (
      offsetDateTime.map(TOML.OffsetDateTime.apply).backtrack |
        dateTime.map(TOML.LocalDateTime.apply).backtrack |
        date.map(TOML.LocalDate.apply).backtrack |
        time.map(TOML.LocalTime.apply).backtrack |
        Float.float.map(TOML.Float.apply).backtrack |
        integer.map(TOML.Integer.apply).backtrack
    )

private val scalarValues: Parser[TOML] =
  (
    Boolean.boolean.map(TOML.Boolean.apply).backtrack |
      string.map(TOML.String.apply).backtrack |
      integerStartValues
  ).withContext("scala-value")

lazy val allValues: Parser[TOML] =
  (scalarValues.backtrack | Array.array)
    .withContext("all-values")
