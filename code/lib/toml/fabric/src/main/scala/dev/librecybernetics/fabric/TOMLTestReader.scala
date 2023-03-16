package dev.librecybernetics.fabric

import fabric.*
import fabric.rw.*

import dev.librecybernetics.types.TOML

given testReader: Reader[TOML] = Reader[TOML](
  {
    case TOML.Boolean(boolean) =>
      Obj("type" -> "bool", "value" -> Str(boolean.toString))
    case TOML.Comment(comment) =>
      Obj("type" -> "comment", "value" -> Str(comment))
    case TOML.String(string)   =>
      Obj("type" -> "string", "value" -> Str(string))
    case TOML.Integer(integer) =>
      Obj("type" -> "integer", "value" -> Str(integer.toString))
    case TOML.Float(double)    =>
      Obj("type" -> "float", "value" -> Str(double.toString))

    case TOML.Array(arr)     => Arr(arr.map(_.json).toVector)
    case TOML.Map(map)       => Obj(map.view.mapValues(_.json).toMap)
  }
)
