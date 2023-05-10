package dev.librecybernetics.parser.rfc3339

import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*

private def intIn(range: Range, context: String): Parser[Int] =
  digit.rep(2, 2).string.map(_.toInt).flatMap {
    case i if range contains i =>
      Parser.pure(i)
    case i                     =>
      Parser.failWith(show"$i out of range(${range.toString}) for $context")
  }
