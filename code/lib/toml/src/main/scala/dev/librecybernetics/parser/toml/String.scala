package dev.librecybernetics.parser.toml

import java.lang.Integer
import scala.language.postfixOps

import cats.parse.Parser
import cats.parse.Accumulator0.charStringAccumulator0

import dev.librecybernetics.parser.*

private val quote       = Parser.char('"')
private val tripleQuote = Parser.string("\"\"\"").withContext("tripleQuote")

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
    .surroundedBy(quote)

val multilineString: Parser[String] =
  (
    newline.?.with1 *> (
      (Parser.not(tripleQuote | backslash).with1 *> Parser.anyChar).rep.string |
        escaped.backtrack | escapedUnicode4.backtrack | escapedUnicode8.backtrack | escapedNewline.backtrack |
        (Parser.char('\"').string <* Parser.peek(tripleQuote)).backtrack // Note: this is from the spec, sorry :C
    ).withContext("multilineString.line").rep.map(_.toList.mkString(""))
  ).surroundedBy(tripleQuote).withContext("multilineString")

val string: Parser[String] =
   // Note: "" is a valid simple string, check triple quote first
   multilineString.backtrack | simpleString.backtrack
