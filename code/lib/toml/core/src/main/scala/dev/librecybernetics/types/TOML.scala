package dev.librecybernetics.types

enum TOML:
  // Simple
  case Boolean(boolean: scala.Boolean)
  case Comment(content: Predef.String)
  case String(content: Predef.String)
  case Integer(content: BigInt)
  case Float(double: Double)
  // Recursive
  case Array(arrays: Seq[TOML])
  case KeyValue(name: Predef.String, content: TOML)
  case Map(map: Predef.Map[Predef.String, TOML])
