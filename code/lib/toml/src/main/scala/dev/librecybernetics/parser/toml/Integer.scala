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

// There is a bug with TLDs /w object dependencies
val integer: Parser[BigInt] =
  Hexadecimal.integer.backtrack |
    Binary.integer.backtrack |
    Octal.integer.backtrack |
    Decimal.integer.backtrack
