package dev.librecybernetics.parser.toml

import scala.language.postfixOps
import cats.parse.Parser

private enum Sign:
  case Plus, Minus

val plus: Parser[Sign.Plus.type]   = Parser.char('+').map(_ => Sign.Plus)
val minus: Parser[Sign.Minus.type] = Parser.char('-').map(_ => Sign.Minus)
val underscore: Parser[Unit]       = Parser.char('_')

object Decimal:
  private val digits: Parser[String] = Parser.charsWhile(_.isDigit)

  val integer: Parser[BigInt] =
    ((plus | minus).?.with1 ~ digits.repSep(underscore)).map {
      case (Some(Sign.Minus), digits) => -BigInt(digits.toList.mkString(""))
      case (_, digits)                => BigInt(digits.toList.mkString(""))
    }

object Hexadecimal:
  private val hexDigits: Set[Char] =
    (('0' to '9') ++ ('a' to 'f') ++ ('A' to 'F')).toSet

  private val hex: Parser[String] = Parser.charsWhile(hexDigits contains _.toLower)

  val integer: Parser[BigInt] =
    (minus.?.with1 ~ (Parser.string("0x") *> hex.repSep(underscore))).map {
      case (Some(Sign.Minus), digits) => BigInt(digits.toList.mkString(""), 16)
      case (_, digits)                => BigInt(digits.toList.mkString(""), 16)
    }

val integer: Parser[BigInt] =
  Hexadecimal.integer.backtrack | Decimal.integer.backtrack
