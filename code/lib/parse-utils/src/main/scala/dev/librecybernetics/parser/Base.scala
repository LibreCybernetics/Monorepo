package dev.librecybernetics.parser

import cats.parse.{Parser, Parser0}

// Char

private[parser] val backslash: Parser[Unit]    = Parser.char('\\')
private[parser] val bracketOpen: Parser[Unit]  = Parser.char('[')
private[parser] val bracketClose: Parser[Unit] = Parser.char(']')
private[parser] val colon: Parser[Unit]        = Parser.char(':')
private[parser] val comma: Parser[Unit]        = Parser.char(',')
private[parser] val dash: Parser[Unit]         = Parser.char('-')
private[parser] val dot: Parser[Unit]          = Parser.char('.')
private[parser] val equal: Parser[Unit]        = Parser.char('=')
private[parser] val hash: Parser[Unit]         = Parser.char('#')
private[parser] val newline: Parser[Unit]      = Parser.char('\n')
private[parser] val space: Parser[Unit]        = Parser.char(' ')
private[parser] val tab: Parser[Unit]          = Parser.char('\t')
private[parser] val underscore: Parser[Unit]   = Parser.char('_')

private[parser] val digit: Parser[Char]      = Parser.charIn(latinDecimalDigits)
private[parser] val whitespace: Parser[Unit] = space | tab

// Spaces / Lines

private[parser] val spaces: Parser0[Int]            = space.rep0.map(_.length)
private[parser] val newlineOrEnd: Parser0[Unit]     = newline | Parser.end
private[parser] val emptyLine: Parser[Unit]         = (spaces.with1 ~ newline).void
private[parser] val anyUntilNewline: Parser[String] = Parser.charsWhile(_ != '\n')
