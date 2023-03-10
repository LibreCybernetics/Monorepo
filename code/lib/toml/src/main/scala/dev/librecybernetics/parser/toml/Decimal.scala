package dev.librecybernetics.parser.toml

import scala.language.postfixOps

import cats.parse.Parser

import dev.librecybernetics.parser.*

object Decimal:
  private val digits: Parser[String] = Parser.charsWhile(_.isDigit)

  val integerSep: Parser[String] = digits.repSep(underscore).underscoresRemoved

  val integer: Parser[BigInt] =
    (sign.?.with1 ~ integerSep).map(toBigInt(10))
