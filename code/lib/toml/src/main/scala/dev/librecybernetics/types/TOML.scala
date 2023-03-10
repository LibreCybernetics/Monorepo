package dev.librecybernetics.types

enum TOML:
  case Comment(content: Predef.String)
  case String(content: Predef.String)
  case Integer(content: BigInt)
  case Float(double: Double)
  case Boolean(boolean: scala.Boolean)
  case Array(arrays: Seq[TOML])
