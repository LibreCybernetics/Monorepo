package dev.librecybernetics.parser.toml.scalar

import scala.language.postfixOps

import cats.parse.{Parser, Parser0}

import dev.librecybernetics.parser.*

private case class GenericInteger(radix: Int, start: Parser0[Unit], digits: Set[Char]) {
  private val digitsParser: Parser[String] =
    Parser.charsWhile(digits contains)

  val literal: Parser[String] =
    digitsParser.repSep(underscore).map(_.toList.mkString(""))

  val integer: Parser[BigInt] =
    (sign.?.with1 ~ (start.with1 *> literal))
      .map(toBigInt(radix))
}
