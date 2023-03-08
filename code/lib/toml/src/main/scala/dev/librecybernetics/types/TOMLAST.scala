package dev.librecybernetics.types

enum TOML:
  case Comment(content: Predef.String)
  case String(content: Predef.String)
  case Integer(content: Int)
  case Float(float: Float)
