package dev.librecybernetics.parser

import cats.data.NonEmptyList
import cats.parse.Parser

extension [A](p: Parser[A])
  def repExactly(i: Int): Parser[NonEmptyList[A]] = p.rep(i, i)
