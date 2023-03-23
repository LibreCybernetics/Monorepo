package dev.librecybernetics.types

import java.time.{
  LocalTime as JLocalTime,
  LocalDate as JLocalDate,
  LocalDateTime as JLocalDateTime,
  OffsetDateTime as JOffsetDateTime
}

enum TOML:
  // Scalar
  case Boolean(boolean: scala.Boolean)
  case Integer(bigInt: BigInt)
  case Float(double: Double)
  case String(string: Predef.String)
  // Temporal
  case LocalTime(localTime: JLocalTime)
  case LocalDate(localDate: JLocalDate)
  case LocalDateTime(localDateTime: JLocalDateTime)
  case OffsetDateTime(offsetDateTime: JOffsetDateTime)
  // Recursive
  case Array(array: Seq[TOML])
  case Map(map: Predef.Map[Predef.String, TOML])
