package dev.librecybernetics.types

enum TOML:
  case Comment(content: String)
  case String(content: String)
  case Integer(content: Int)
  case Float(float: Float)
