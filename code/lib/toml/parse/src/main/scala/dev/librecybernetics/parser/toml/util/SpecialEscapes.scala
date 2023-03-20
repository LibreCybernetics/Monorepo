package dev.librecybernetics.parser.toml.util

import cats.parse.Parser

import dev.librecybernetics.parser.*

private[toml] val escaped: Parser[Char] =
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

private[toml] val escapedNewline: Parser[String] =
  (Parser.string("\\\n").map(_ => "") <* emptyLine.backtrack.rep0 <* spaces)
    .withContext("escapedNewline")
