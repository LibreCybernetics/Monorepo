package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML

private val assignment: Parser[Unit] =
  equal.surroundedBy(spaces)

val keyValue: Parser[TOML.Map] =
  (
    simpleKey,
    assignment *> allValues <* spaces <* comment.?
  ).mapN((k, v) => TOML.Map(Map(k -> v)))

val keyValueOrMap: Parser[TOML.Map] =
  keyValue.backtrack | keyMap
