package dev.librecybernetics.parser.toml

import cats.parse.Parser

private enum Sign:
  case Plus, Minus

val plus: Parser[Sign.Plus.type]   = Parser.char('+').map(_ => Sign.Plus)
val minus: Parser[Sign.Minus.type] = Parser.char('-').map(_ => Sign.Minus)
val underscore: Parser[Unit]       = Parser.char('_')

val digits: Parser[String] = Parser.charsWhile(_.isDigit)

val integer: Parser[BigInt] =
  ((plus | minus).?.with1 ~ digits.repSep(underscore)).map {
    case (Some(Sign.Minus), digits) => -BigInt(digits.toList.mkString(""))
    case (_, digits)                => BigInt(digits.toList.mkString(""))
  }
