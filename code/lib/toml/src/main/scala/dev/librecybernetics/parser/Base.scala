package dev.librecybernetics.parser

import cats.parse.{Parser, Parser0}

// Data

val hexDigit: Set[Char] = (('0' to '9') ++ ('a' to 'f') ++ ('A' to 'F')).toSet

//
// Parsers
//

// Char

val backslash: Parser[Unit]  = Parser.char('\\')
val comma: Parser[Unit]      = Parser.char(',')
val hash: Parser[Unit]       = Parser.char('#')
val newline: Parser[Unit]    = Parser.char('\n')
val space: Parser[Unit]      = Parser.char(' ')
val underscore: Parser[Unit] = Parser.char('_')

// Spaces / Lines

val spaces: Parser0[Int]            = space.rep0.map(_.length)
val newlineOrEnd: Parser0[Unit] = newline | Parser.end
val emptyLine: Parser[Unit]    = (spaces.with1 ~ newline).void
val anyUntilNewline: Parser[String] = Parser.charsWhile(_ != '\n')
