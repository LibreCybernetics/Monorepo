package dev.librecybernetics.parser.toml.base

import cats.parse.Parser

object Boolean:
  val boolean: Parser[Boolean] =
    Parser.string("true").map(_ => true) |
      Parser.string("false").map(_ => false)
