package dev.librecybernetics.parser.toml.util

import cats.implicits.*
import cats.parse.Accumulator0.charStringAccumulator0
import cats.parse.Parser

import dev.librecybernetics.parser.*

private[toml] val escapedUnicode4: Parser[Char] =
  Parser.string("\\u") *>
    Parser
      .charIn(hexDigit)
      .repExactlyAs(4)(using charStringAccumulator0)
      .map(Integer.parseInt(_, 16))
      .withString
      .flatMap {
        case (i, _) if i.isValidChar && !i.toChar.isSurrogate =>
          Parser.pure(i.toChar)
        case (_, s)                  =>
          Parser.failWith(show"Invalid unicode codepoint: \\u$s")
      }

private[toml] val escapedUnicode8: Parser[Char] =
  Parser.string("\\U") *>
    Parser
      .charIn(hexDigit)
      .repExactlyAs(8)(using charStringAccumulator0)
      .map(Integer.parseInt(_, 16))
      .withString
      .flatMap {
        case (i, _) if i.isValidChar && !i.toChar.isSurrogate  =>
          Parser.pure(i.toChar)
        case (_, s)                  =>
          Parser.failWith(show"Invalid unicode codepoint: \\U$s")
      }
