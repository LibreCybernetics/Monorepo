package dev.librecybernetics.parser

import cats.parse.{Parser, Parser0}

// Char

val backslash: Parser[Unit]    = Parser.char('\\')
val bracketOpen: Parser[Unit]  = Parser.char('[')
val bracketClose: Parser[Unit] = Parser.char(']')
val colon: Parser[Unit]        = Parser.char(':')
val comma: Parser[Unit]        = Parser.char(',')
val dash: Parser[Unit]         = Parser.char('-')
val dot: Parser[Unit]          = Parser.char('.')
val equal: Parser[Unit]        = Parser.char('=')
val hash: Parser[Unit]         = Parser.char('#')
val newline: Parser[Unit]      = Parser.char('\n')
val space: Parser[Unit]        = Parser.char(' ')
val underscore: Parser[Unit]   = Parser.char('_')

val digit: Parser[Char] = Parser.charIn(latinDecimalDigits)

// Spaces / Lines

val spaces: Parser0[Int]            = space.rep0.map(_.length)
val newlineOrEnd: Parser0[Unit]     = newline | Parser.end
val emptyLine: Parser[Unit]         = (spaces.with1 ~ newline).void
val anyUntilNewline: Parser[String] = Parser.charsWhile(_ != '\n')
