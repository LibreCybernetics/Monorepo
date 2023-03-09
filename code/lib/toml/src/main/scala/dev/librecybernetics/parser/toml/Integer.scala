package dev.librecybernetics.parser.toml

import scala.language.postfixOps
import cats.parse.Parser
import cats.data.NonEmptyList

private enum Sign:
  case Plus, Minus

val plus: Parser[Sign.Plus.type]   = Parser.char('+').map(_ => Sign.Plus)
val minus: Parser[Sign.Minus.type] = Parser.char('-').map(_ => Sign.Minus)
val underscore: Parser[Unit]       = Parser.char('_')

private def toBigInt(radix: Int)(
  s: Option[Sign],
  nel: NonEmptyList[String]
): BigInt =
  val transformSign: BigInt => BigInt = s
    .collect { case Sign.Minus => (bi: BigInt) => -bi }
    .getOrElse(identity[BigInt])

  transformSign(BigInt(nel.toList.mkString(""), radix))

object Octal:
  private val octalDigits: Set[Char] = ('0' to '7').toSet
  private val octal: Parser[String]  = Parser.charsWhile(octalDigits contains)

  val integer: Parser[BigInt] =
    (minus.?.with1 ~ (Parser.string("0o") *> octal.repSep(underscore)))
      .map(toBigInt(8))

object Decimal:
  private val digits: Parser[String] = Parser.charsWhile(_.isDigit)

  val integer: Parser[BigInt] =
    ((plus | minus).?.with1 ~ digits.repSep(underscore))
      .map(toBigInt(10))

object Hexadecimal:
  private val hexDigits: Set[Char] =
    (('0' to '9') ++ ('a' to 'f') ++ ('A' to 'F')).toSet

  private val hex: Parser[String] = Parser.charsWhile(hexDigits contains)

  val integer: Parser[BigInt] =
    (minus.?.with1 ~ (Parser.string("0x") *> hex.repSep(underscore)))
      .map(toBigInt(16))

val integer: Parser[BigInt] =
  Octal.integer.backtrack | Hexadecimal.integer.backtrack | Decimal.integer.backtrack
