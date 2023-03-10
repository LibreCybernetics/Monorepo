package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

object Array:
  val bi = (spaces.with1 *> Parser.char('[') <* spaces).withContext("bi")
  val be = (spaces.with1 *> Parser.char(']') <* spaces).withContext("be")

  lazy val scalarArray: Parser[TOML] =
    scalarValues
      .repSep(comma.surroundedBy(spaces).backtrack)
      .between(bi, be)
      .map(arr => TOML.Array(arr.toList))
      .withContext("scalarArray")

  lazy val array: Parser[TOML] = Parser.recursive[TOML] { p =>
    scalarArray.backtrack | p
      .repSep(comma.surroundedBy(spaces).backtrack)
      .between(bi, be)
      .map { (arr: NonEmptyList[TOML]) => TOML.Array(arr.toList) }
      .withContext("recursiveArray")
  }
