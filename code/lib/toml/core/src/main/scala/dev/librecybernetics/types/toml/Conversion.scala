package dev.librecybernetics.types.toml

import java.time.{LocalTime, LocalDate, LocalDateTime, OffsetDateTime}

import dev.librecybernetics.types.TOML

// Temporal

given Conversion[LocalTime, TOML.LocalTime] with
  override def apply(x: LocalTime): TOML.LocalTime = TOML.LocalTime(x)

given Conversion[LocalDate, TOML.LocalDate] with
  override def apply(x: LocalDate): TOML.LocalDate = TOML.LocalDate(x)

given Conversion[LocalDateTime, TOML.LocalDateTime] with
  override def apply(x: LocalDateTime): TOML.LocalDateTime = TOML.LocalDateTime(x)

given Conversion[OffsetDateTime, TOML.OffsetDateTime] with
  override def apply(x: OffsetDateTime): TOML.OffsetDateTime = TOML.OffsetDateTime(x)

// Collection

given Conversion[Seq[TOML], TOML.ScalarArray] with
  override def apply(x: Seq[TOML]): TOML.ScalarArray = TOML.ScalarArray(x)
given Conversion[Seq[TOML], TOML.ArrayOfTables] with
  override def apply(x: Seq[TOML]): TOML.ArrayOfTables = TOML.ArrayOfTables(x)

given Conversion[(String, String), TOML.Map] with
  override def apply(t: (String, String)): TOML.Map =
    val (k, v) = t
    TOML.Map(Map(k -> TOML.String(v)))
