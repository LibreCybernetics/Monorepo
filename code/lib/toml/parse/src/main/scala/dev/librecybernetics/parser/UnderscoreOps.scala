package dev.librecybernetics.parser

import cats.data.NonEmptyList
import cats.parse.Parser

extension (p: Parser[NonEmptyList[String]])
  private[parser] def underscoresRemoved: Parser[String] =
    p.map(_.toList.mkString(""))
