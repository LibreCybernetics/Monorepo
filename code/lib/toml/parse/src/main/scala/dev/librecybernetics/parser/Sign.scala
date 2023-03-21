package dev.librecybernetics.parser

import cats.parse.Parser

private[parser] enum Sign:
  case Plus, Minus

private[parser] val plus: Parser[Sign.Plus.type]   = Parser.char('+').map(_ => Sign.Plus)
private[parser] val minus: Parser[Sign.Minus.type] = Parser.char('-').map(_ => Sign.Minus)

private[parser] val sign: Parser[Sign] = plus | minus
