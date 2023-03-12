package dev.librecybernetics.parser.toml

import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

val keyValue: Parser[TOML.KeyValue] =
  (
    (bareKey.backtrack | simpleLiteral.backtrack | simpleString.backtrack),
    spaces.with1 *> Parser.char('=') *> spaces *> allValues
  ).mapN(TOML.KeyValue.apply)
