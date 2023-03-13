package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

object Array:
  val bracketStart: Parser[Unit] = bracketOpen.surroundedBy(spaces)
  val bracketEnd: Parser[Unit] = bracketClose.surroundedBy(spaces)

  lazy val scalarArray: Parser[TOML] =
    scalarValues
      .repSep(comma.surroundedBy(spaces).backtrack)
      .between(bracketStart, bracketEnd)
      .map(arr => TOML.Array(arr.toList))
      .withContext("scalarArray")

  lazy val array: Parser[TOML] = Parser.recursive[TOML] { p =>
    scalarArray.backtrack | p
      .repSep(comma.surroundedBy(spaces).backtrack)
      .between(bracketStart, bracketClose)
      .map { (arr: NonEmptyList[TOML]) => TOML.Array(arr.toList) }
      .withContext("recursiveArray")
  }
