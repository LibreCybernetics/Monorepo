package dev.librecybernetics.types

import java.time.{
  LocalTime as JLocalTime,
  LocalDate as JLocalDate,
  LocalDateTime as JLocalDateTime,
  OffsetDateTime as JOffsetDateTime
}

import cats.Semigroup
import cats.implicits.*

enum TOML:
  // Simple
  case Boolean(boolean: scala.Boolean)
  case Comment(content: Predef.String)
  case String(content: Predef.String)
  case Integer(content: BigInt)
  case Float(double: Double)
  // Temporal
  case LocalTime(localTime: JLocalTime)
  case LocalDate(localDate: JLocalDate)
  case LocalDateTime(localDateTime: JLocalDateTime)
  case OffsetDateTime(zonedDateTime: JOffsetDateTime)
  // Recursive
  case Array(arrays: Seq[TOML])
  case Map(map: Predef.Map[Predef.String, TOML])
