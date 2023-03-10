package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.parse.Parser

extension (p: Parser[NonEmptyList[String]])
  def underscoresRemoved: Parser[String] =
    p.map(_.toList.mkString(""))
