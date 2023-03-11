package dev.librecybernetics.parser.toml

import java.lang.Integer
import scala.language.postfixOps

import cats.parse.Parser
import cats.parse.Accumulator0.charStringAccumulator0

import dev.librecybernetics.parser.*

private val quote = Parser.char('"')

private val escaped =
  Map(
    "\\b"  -> '\b',
    "\\t"  -> '\t',
    "\\n"  -> '\n',
    "\\f"  -> '\f',
    "\\r"  -> '\r',
    "\\\"" -> '"',
    "\\\\" -> '\\'
  ).map { (s, c) =>
    Parser.string(s).map(_ => c)
  }.reduceOption(_.backtrack | _.backtrack)
    .getOrElse(Parser.fail)

private val escapedUnicode4 =
  Parser.string("\\u") *>
    Parser
      .charIn(hexDigit)
      .repExactlyAs(4)(using charStringAccumulator0)
      .map(Integer.parseInt(_, 16).toChar)

private val escapedUnicode8 =
  Parser.string("\\U") *>
    Parser
      .charIn(hexDigit)
      .repExactlyAs(8)(using charStringAccumulator0)
      .map(Integer.parseInt(_, 16).toChar)

val string: Parser[String] =
  (
    Parser.charsWhile(c => !(Set('"', '\\') contains c)).backtrack |
      escaped.backtrack | escapedUnicode4.backtrack | escapedUnicode8.backtrack
  ).rep
    .map(_.toList.mkString(""))
    .surroundedBy(quote)
