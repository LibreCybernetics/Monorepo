package dev.librecybernetics.parser.toml.base

import cats.parse.Parser

// Note: u000A Line Feed (LF) is \n as such it isn't included
private val disallowedChars: Set[Char] =
  ('\u0000' to '\u0008').toSet ++ ('\u000B' to '\u001F').toSet + '\u007F'

extension (p: Parser[String])
  def checkDisallowedChars: Parser[String] =
    p.flatMap { s =>
      s find disallowedChars.contains match
        case Some(disallowed) =>
          val char: String = disallowed.toInt.toHexString
          Parser.failWith(
            s"Disallowed control character detected: $char"
          )
        case None             =>
          Parser.pure(s)
    }
