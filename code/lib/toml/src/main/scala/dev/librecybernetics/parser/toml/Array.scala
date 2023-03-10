package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.types.TOML


object Array:
  val space = Parser.char(' ').rep0.backtrack
  val comma = Parser.char(',').backtrack
  val bi = (space.with1 *> Parser.char('[') <* space).withContext("bi").backtrack
  val be = (space.with1 *> Parser.char(']') <* space).withContext("be").backtrack

  lazy val scalarArray: Parser[TOML] =
    Values.scalarValues.backtrack.withContext("scalar")
      .repSep(comma.surroundedBy(space).backtrack)
      .between(bi, be)
      .backtrack
      .map(arr => TOML.Array(arr.toList))
      .withContext("scalarArray")

  lazy val array: Parser[TOML] = Parser.recursive[TOML] { p =>
    scalarArray.backtrack | p.withContext("recursiveArray")
        .backtrack
        .repSep(comma.surroundedBy(space))
        .between(bi, be)
        .map { (arr: NonEmptyList[TOML]) => TOML.Array(arr.toList) }
  }