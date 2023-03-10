package dev.librecybernetics.parser

import cats.parse.{Parser, Parser0}

val comma: Parser[Unit]      = Parser.char(',')
val hash: Parser[Unit]       = Parser.char('#')
val newline: Parser[Unit]    = Parser.char('\n')
val space: Parser[Unit]      = Parser.char(' ')
val underscore: Parser[Unit] = Parser.char('_')

val newlineOrEnd: Parser0[Unit] = newline | Parser.end

val spaces: Parser0[Int] = space.rep0.map(_.length)

val anyUntilNewline: Parser[String] = Parser.charsWhile(_ != '\n')
