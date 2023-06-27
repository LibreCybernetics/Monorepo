package dev.librecybernetics.fabric

import fabric.*
import fabric.rw.*

import dev.librecybernetics.types.TOML

given reader: Reader[TOML] = new Reader[TOML]:
  override def read(t: TOML): Json = t match
    // Simple
    case TOML.Boolean(boolean) => Bool(boolean)
    case TOML.String(string)   => Str(string)
    case TOML.Integer(integer) => NumInt(integer.toLong)
    case TOML.Float(double)    => NumDec(BigDecimal(double))

    // Temporal
    case TOML.LocalTime(localTime)          => Str(localTime.toString)
    case TOML.LocalDate(localDate)          => Str(localDate.toString)
    case TOML.LocalDateTime(localDateTime)  => Str(localDateTime.toString)
    case TOML.OffsetDateTime(zonedDateTime) => Str(zonedDateTime.toString)

    // Recursive
    case TOML.ScalarArray(arr)   => Arr(arr.map(read).toVector)
    case TOML.ArrayOfTables(arr) => Arr(arr.map(read).toVector)
    case TOML.Map(map)           => Obj(map.view.mapValues(read).toMap)
  end read
