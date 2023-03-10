package dev.librecybernetics.parser.toml

import scala.language.postfixOps

import cats.parse.Parser

import dev.librecybernetics.parser.*

object Octal:
  private val octalDigits: Set[Char] = ('0' to '7').toSet
  private val octal: Parser[String]  = Parser.charsWhile(octalDigits contains)

  val integerSep: Parser[String] = octal.repSep(underscore).underscoresRemoved

  val integer: Parser[BigInt] =
    (minus.?.with1 ~ (Parser.string("0o") *> integerSep))
      .map(toBigInt(8))
