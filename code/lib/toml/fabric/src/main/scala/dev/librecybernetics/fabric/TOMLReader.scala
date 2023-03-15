package dev.librecybernetics.fabric

import fabric.*
import fabric.rw.*

import dev.librecybernetics.types.TOML

given reader: Reader[TOML] = Reader[TOML](
  {
    case TOML.Boolean(boolean) => Bool(boolean)
    case TOML.Comment(string)  => Str(string)
    case TOML.String(string)   => Str(string)
    case TOML.Integer(integer) => NumInt(integer.toLong)
    case TOML.Float(double)    => NumDec(BigDecimal(double))
    case TOML.Array(arr)       => Arr(arr.map(_.json).toVector)
    case TOML.KeyValue(k, v)   => Obj(k -> v.json)
    case TOML.Map(map)         => Obj(map.view.mapValues(_.json).toMap)
  }
)
