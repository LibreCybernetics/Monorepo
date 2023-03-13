package dev.librecybernetics.parser.toml

import scala.language.postfixOps

import cats.parse.Parser
import cats.data.NonEmptyList

import dev.librecybernetics.parser.*

private def toBigInt(radix: Int)(
    sign: Option[Sign],
    string: String
): BigInt =
  val transformSign = sign
    .collect { case Sign.Minus => (bi: BigInt) => -bi }
    .getOrElse(identity[BigInt])
  transformSign(BigInt(string, radix))

val Binary = GenericInteger(
  2,
  Parser.string("0b"),
  ('0' to '1').toSet
)

val Octal = GenericInteger(
  8,
  Parser.string("0o"),
  ('0' to '7').toSet
)

val Decimal = GenericInteger(
  10,
  Parser.unit,
  latinDecimalDigits
)

val Hexadecimal = GenericInteger(
  16,
  Parser.string("0x"),
  hexDigit
)

private val nonDecimal: Parser[Unit] =
  (sign.?.with1 ~ Parser.char('0') ~ Parser.charIn(Set('b', 'o', 'x'))).void

val integer: Parser[BigInt] =
  (
    Parser.peek(nonDecimal).with1 *>
      Hexadecimal.integer.backtrack |
      Octal.integer.backtrack |
      Binary.integer.backtrack
  ).backtrack | Decimal.integer.backtrack
