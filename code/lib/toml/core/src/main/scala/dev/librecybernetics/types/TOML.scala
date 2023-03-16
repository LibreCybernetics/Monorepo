package dev.librecybernetics.types

import cats.Semigroup
import cats.implicits.*

enum TOML:
  // Simple
  case Boolean(boolean: scala.Boolean)
  case Comment(content: Predef.String)
  case String(content: Predef.String)
  case Integer(content: BigInt)
  case Float(double: Double)
  // Recursive
  case Array(arrays: Seq[TOML])
  case Map(map: Predef.Map[Predef.String, TOML])
