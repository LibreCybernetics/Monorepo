package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

private val assignment: Parser[Unit] =
  equal.surroundedBy(spaces)

val keyValue: Parser[TOML.KeyValue] =
  (
    simpleKey,
    assignment *> allValues
  ).mapN(TOML.KeyValue.apply)

val keyValueOrMap: Parser[TOML] =
  keyValue.backtrack | keyMap