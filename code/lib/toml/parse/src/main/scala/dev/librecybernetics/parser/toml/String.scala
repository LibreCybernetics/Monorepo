package dev.librecybernetics.parser.toml

import java.lang.Integer
import scala.language.postfixOps

import cats.parse.Parser
import cats.parse.Accumulator0.charStringAccumulator0

import dev.librecybernetics.parser.*

private val singleQuote = Parser.char('\'')
private val doubleQuote = Parser.char('"')

private val tripleSingleQuote = Parser.string("'''").withContext("tripleSingleQuote")
private val tripleDoubleQuote = Parser.string("\"\"\"").withContext("tripleDoubleQuote")

val simpleLiteral: Parser[String] =
  Parser
    .charsWhile(_ != '\'')
    .surroundedBy(singleQuote)

val multilineLiteral: Parser[String] =
  (
    newline.?.with1 *> (
      (Parser.not(tripleSingleQuote).with1 *> Parser.anyChar).rep.string |
        (Parser.char('\'').string <* Parser.peek(tripleSingleQuote)).backtrack // Note: this is from the spec, sorry :C
    ).withContext("multilineLiteral.line")
      .rep
      .map(_.toList.mkString(""))
  ).surroundedBy(tripleSingleQuote).withContext("multilineLiteral")

private val escaped: Parser[Char] =
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

private val escapedNewline: Parser[String] =
  (Parser.string("\\\n").map(_ => "") <* emptyLine.backtrack.rep0 <* spaces)
    .withContext("escapedNewline")

private val escapedUnicode4: Parser[Char] =
  Parser.string("\\u") *>
    Parser
      .charIn(hexDigit)
      .repExactlyAs(4)(using charStringAccumulator0)
      .map(Integer.parseInt(_, 16).toChar)

private val escapedUnicode8: Parser[Char] =
  Parser.string("\\U") *>
    Parser
      .charIn(hexDigit)
      .repExactlyAs(8)(using charStringAccumulator0)
      .map(Integer.parseInt(_, 16).toChar)

val simpleString: Parser[String] =
  (
    Parser.charsWhile(c => !(Set('"', '\\') contains c)).backtrack |
      escaped.backtrack | escapedUnicode4.backtrack | escapedUnicode8.backtrack
  ).rep
    .map(_.toList.mkString(""))
    .surroundedBy(doubleQuote)

val multilineString: Parser[String] =
  (
    newline.?.with1 *> (
      (Parser.not(tripleDoubleQuote | backslash).with1 *> Parser.anyChar).rep.string |
        escaped.backtrack | escapedUnicode4.backtrack | escapedUnicode8.backtrack | escapedNewline.backtrack |
        (Parser.char('\"').string <* Parser.peek(tripleDoubleQuote)).backtrack // Note: this is from the spec, sorry :C
    ).withContext("multilineString.line").rep.map(_.toList.mkString(""))
  ).surroundedBy(tripleDoubleQuote).withContext("multilineString")

val string: Parser[String] =
  // Note: "" is a valid simple string, check triple quote first
  multilineLiteral.backtrack | simpleLiteral.backtrack |
    multilineString.backtrack | simpleString.backtrack
