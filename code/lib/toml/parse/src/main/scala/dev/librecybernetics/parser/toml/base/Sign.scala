package dev.librecybernetics.parser.toml.base

import cats.parse.Parser

enum Sign:
  case Plus, Minus

val plus: Parser[Sign.Plus.type]   = Parser.char('+').map(_ => Sign.Plus)
val minus: Parser[Sign.Minus.type] = Parser.char('-').map(_ => Sign.Minus)

val sign: Parser[Sign] = plus | minus
