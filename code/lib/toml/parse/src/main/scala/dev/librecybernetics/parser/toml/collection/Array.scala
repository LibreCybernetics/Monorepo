package dev.librecybernetics.parser.toml.collection

import cats.data.NonEmptyList
import cats.parse.{Parser, Parser0}

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.*
import dev.librecybernetics.parser.toml.scalarValues
import dev.librecybernetics.types.TOML

private[toml] object Array:
  val bracketStart: Parser[Unit]  =
    bracketOpen
      .surroundedBy((emptyOrComment).rep0 ~ spaces)
      .withContext("bracket-start")
  val bracketEnd: Parser[Unit]    =
    bracketClose
      .between(emptyOrComment.rep0 ~ spaces, Parser.unit)
      .withContext("bracket-end")
  val trailingComma: Parser[Unit] =
    ((emptyOrComment.rep0 ~ spaces).with1 ~ comma ~ emptyOrComment.rep0)
      .withContext("trailing-comma")
      .backtrack
      .void

  val scalarArray: Parser[TOML] =
    (bracketStart *> scalarValues
      .repSep0(comma.surroundedBy(emptyOrComment.rep0 ~ spaces).backtrack) <*
      trailingComma.? <* bracketEnd)
      // TODO: Upstream a between1(Parser[Unit], Parser[Unit]): Parser[A] combinator
      // .between(bracketStart, trailingComma.? ~ bracketEnd)
      .map(TOML.ScalarArray(_))
      .withContext("scalarArray")

  lazy val array: Parser[TOML] = Parser.recursive[TOML] { p =>
    scalarArray.backtrack |
      (bracketStart *> (scalarValues | p).repSep0(
        comma.surroundedBy(spaces).backtrack
      ) <* trailingComma.? <* bracketEnd)
        // TODO: Upstream a between1(Parser[Unit], Parser[Unit]): Parser[A] combinator
        // .between(bracketStart, trailingComma.? ~ bracketEnd)
        .map(TOML.ScalarArray(_))
        .withContext("recursiveArray")
  }
