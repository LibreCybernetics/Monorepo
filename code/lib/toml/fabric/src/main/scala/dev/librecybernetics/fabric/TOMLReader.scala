package dev.librecybernetics.fabric

import fabric.*
import fabric.rw.*

import dev.librecybernetics.types.TOML

given reader: Reader[TOML] = Reader[TOML](
  {
    // Simple
    case TOML.Boolean(boolean) => Bool(boolean)
    case TOML.Comment(string)  => Str(string)
    case TOML.String(string)   => Str(string)
    case TOML.Integer(integer) => NumInt(integer.toLong)
    case TOML.Float(double)    => NumDec(BigDecimal(double))

    // Temporal
    case TOML.LocalTime(localTime)         => Str(localTime.toString)
    case TOML.LocalDate(localDate)         => Str(localDate.toString)
    case TOML.LocalDateTime(localDateTime) => Str(localDateTime.toString)
    case TOML.OffsetDateTime(zonedDateTime) => Str(zonedDateTime.toString)

    // Recursive
    case TOML.Array(arr) => Arr(arr.map(_.json).toVector)
    case TOML.Map(map)   => Obj(map.view.mapValues(_.json).toMap)
  }
)
