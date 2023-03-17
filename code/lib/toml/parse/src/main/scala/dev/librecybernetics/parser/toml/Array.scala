package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*
import dev.librecybernetics.types.TOML

object Array:
  val bracketStart: Parser[Unit]  = bracketOpen.surroundedBy(spaces)
  val bracketEnd: Parser[Unit]    = bracketClose.surroundedBy(spaces)
  val trailingComma: Parser[Unit] = (emptyLine.rep0.with1 *> comma <* emptyLine.rep0).backtrack

  lazy val scalarArray: Parser[TOML] =
    scalarValues
      .repSep(comma.surroundedBy(spaces).backtrack)
      .between(bracketStart, trailingComma.? ~ bracketEnd)
      .map(arr => TOML.Array(arr.toList))
      .withContext("scalarArray")

  lazy val array: Parser[TOML] = Parser.recursive[TOML] { p =>
    scalarArray.backtrack | p
      .repSep(comma.surroundedBy(spaces).backtrack)
      .between(bracketStart, trailingComma.? ~ bracketEnd)
      .map { (arr: NonEmptyList[TOML]) => TOML.Array(arr.toList) }
      .withContext("recursiveArray")
  }
