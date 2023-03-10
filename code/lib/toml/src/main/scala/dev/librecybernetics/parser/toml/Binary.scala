package dev.librecybernetics.parser.toml

import scala.language.postfixOps

import cats.parse.Parser

import dev.librecybernetics.parser.*

object Binary:
  private val binaryDigits: Set[Char] = ('0' to '1').toSet
  private val binary: Parser[String]  = Parser.charsWhile(binaryDigits contains)

  val integerSep: Parser[String] = binary.repSep(underscore).underscoresRemoved

  val integer: Parser[BigInt] =
    (minus.?.with1 ~ (Parser.string("0b") *> integerSep))
      .map(toBigInt(2))
