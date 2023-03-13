package dev.librecybernetics.types

enum TOML:
  // Simple
  case Comment(content: Predef.String)
  case String(content: Predef.String)
  case Integer(content: BigInt)
  case Float(double: Double)
  case Boolean(boolean: scala.Boolean)
  // Recursive
  case Array(arrays: Seq[TOML])
  case KeyValue(name: Predef.String, content: TOML)
  case Map(map: Predef.Map[Predef.String, TOML])
