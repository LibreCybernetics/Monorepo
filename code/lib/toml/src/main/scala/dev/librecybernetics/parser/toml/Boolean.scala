package dev.librecybernetics.parser.toml

import cats.parse.Parser

object Boolean:
  val boolean: Parser[Boolean] =
    val trueP = Parser.string("true").map(_ => true)
    val falseP = Parser.string("false").map(_ => false)
    trueP | falseP
