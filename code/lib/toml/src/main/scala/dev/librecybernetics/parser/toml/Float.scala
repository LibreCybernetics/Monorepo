package dev.librecybernetics.parser.toml

import cats.parse.{Parser, Parser0}
import cats.implicits.*

object Float:
  lazy val tomlFloat: Parser0[BigInt] = Decimal.integer
