package dev.librecybernetics.parser.toml.scalar

import cats.parse.Parser

private[toml] val boolean: Parser[Boolean] =
  Parser.fromStringMap(
    Map(
      "false" -> false,
      "true"  -> true
    )
  )
