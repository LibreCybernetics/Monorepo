package dev.librecybernetics.parser.toml

import scala.language.postfixOps
import cats.parse.Parser
import cats.data.NonEmptyList

private enum Sign:
  case Plus, Minus

val plus: Parser[Sign.Plus.type]   = Parser.char('+').map(_ => Sign.Plus)
val minus: Parser[Sign.Minus.type] = Parser.char('-').map(_ => Sign.Minus)
val sign: Parser[Sign] = plus | minus
val underscore: Parser[Unit]       = Parser.char('_')

private def toBigInt(radix: Int)(
  sign: Option[Sign],
  string: String
): BigInt =
  val transformSign = sign
    .collect { case Sign.Minus => (bi: BigInt) => -bi }
    .getOrElse(identity[BigInt])
  transformSign(BigInt(string, radix))

extension (p: Parser[NonEmptyList[String]])
  def underscoresRemoved: Parser[String] =
    p.map(_.toList.mkString(""))

object Octal:
  private val octalDigits: Set[Char] = ('0' to '7').toSet
  private val octal: Parser[String]  = Parser.charsWhile(octalDigits contains)

  val integerSep: Parser[String] = octal.repSep(underscore).underscoresRemoved

  val integer: Parser[BigInt] =
    (minus.?.with1 ~ (Parser.string("0o") *> integerSep))
      .map(toBigInt(8))

object Decimal:
  private val digits: Parser[String] = Parser.charsWhile(_.isDigit)

  val integerSep: Parser[String] = digits.repSep(underscore).underscoresRemoved

  val integer: Parser[BigInt] =
    (sign.?.with1 ~ integerSep).map(toBigInt(10))

object Hexadecimal:
  private val hexDigits: Set[Char] =
    (('0' to '9') ++ ('a' to 'f') ++ ('A' to 'F')).toSet

  private val hex: Parser[String] = Parser.charsWhile(hexDigits contains)

  val integerSep: Parser[String] = hex.repSep(underscore).underscoresRemoved

  val integer: Parser[BigInt] =
    (minus.?.with1 ~ (Parser.string("0x") *> integerSep))
      .map(toBigInt(16))

// There is a bug with TLDs /w object dependencies
object Integer:
  val integer: Parser[BigInt] =
    Octal.integer.backtrack | Hexadecimal.integer.backtrack | Decimal.integer.backtrack