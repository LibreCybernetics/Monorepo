package dev.librecybernetics.fabric

import fabric.*
import fabric.rw.*

import dev.librecybernetics.types.TOML

given testReader: Reader[TOML] = new Reader[TOML]:
  override def read(t: TOML): Json = t match
    // Simple
    case TOML.Boolean(boolean) =>
      Obj("type" -> "bool", "value" -> Str(boolean.toString))
    case TOML.String(string)   =>
      Obj("type" -> "string", "value" -> Str(string))
    case TOML.Integer(integer) =>
      Obj("type" -> "integer", "value" -> Str(integer.toString))
    case TOML.Float(double)    =>
      Obj("type" -> "float", "value" -> Str(double.toString))

    // Temporal
    case TOML.LocalTime(localTime)          =>
      Obj("type" -> "time-local", "value" -> Str(localTime.toString))
    case TOML.LocalDate(localDate)          =>
      Obj("type" -> "time-date", "value" -> Str(localDate.toString))
    case TOML.LocalDateTime(localDateTime)  =>
      Obj("type" -> "datetime-local", "value" -> Str(localDateTime.toString))
    case TOML.OffsetDateTime(zonedDateTime) =>
      Obj("type" -> "datetime", "value" -> Str(zonedDateTime.toString))

    // Recursive
    case TOML.ScalarArray(arr)   => Arr(arr.map(read).toVector)
    case TOML.ArrayOfTables(arr) => Arr(arr.map(read).toVector)
    case TOML.Map(map)           => Obj(map.view.mapValues(read).toMap)
  end read
