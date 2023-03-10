package dev.librecybernetics.parser.toml

import scala.language.postfixOps

import cats.parse.Parser

import dev.librecybernetics.parser.*

object Hexadecimal:
  private val hexDigits: Set[Char] =
    (('0' to '9') ++ ('a' to 'f') ++ ('A' to 'F')).toSet

  private val hex: Parser[String] = Parser.charsWhile(hexDigits contains)

  val integerSep: Parser[String] = hex.repSep(underscore).underscoresRemoved

  val integer: Parser[BigInt] =
    (minus.?.with1 ~ (Parser.string("0x") *> integerSep))
      .map(toBigInt(16))
