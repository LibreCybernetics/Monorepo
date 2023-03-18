package dev.librecybernetics.types.toml

import java.time.{LocalTime, LocalDate, LocalDateTime, OffsetDateTime}

import dev.librecybernetics.types.TOML

given Conversion[Boolean, TOML.Boolean] with
  override def apply(n: Boolean): TOML.Boolean = TOML.Boolean(n)

given Conversion[Byte, TOML.Integer] with
  override def apply(x: Byte): TOML.Integer = TOML.Integer(BigInt(x))

given Conversion[Short, TOML.Integer] with
  override def apply(x: Short): TOML.Integer = TOML.Integer(BigInt(x))

given Conversion[Int, TOML.Integer] with
  override def apply(x: Int): TOML.Integer = TOML.Integer(BigInt(x))

given Conversion[Long, TOML.Integer] with
  override def apply(x: Long): TOML.Integer = TOML.Integer(BigInt(x))

given Conversion[BigInt, TOML.Integer] with
  override def apply(x: BigInt): TOML.Integer = TOML.Integer(x)

given Conversion[Float, TOML.Float] with
  override def apply(x: Float): TOML.Float = TOML.Float(x.toDouble)

given Conversion[Double, TOML.Float] with
  override def apply(x: Double): TOML.Float = TOML.Float(x)

given Conversion[BigDecimal, TOML.Float] with
  override def apply(x: BigDecimal): TOML.Float = TOML.Float(x.toDouble)

given Conversion[String, TOML.String] with
  override def apply(x: String): TOML.String = TOML.String(x)

given Conversion[LocalTime, TOML.LocalTime] with
  override def apply(x: LocalTime): TOML.LocalTime = TOML.LocalTime(x)

given Conversion[LocalDate, TOML.LocalDate] with
  override def apply(x: LocalDate): TOML.LocalDate = TOML.LocalDate(x)

given Conversion[LocalDateTime, TOML.LocalDateTime] with

  override def apply(x: LocalDateTime): TOML.LocalDateTime = TOML.LocalDateTime(x)

given Conversion[OffsetDateTime, TOML.OffsetDateTime] with

  override def apply(x: OffsetDateTime): TOML.OffsetDateTime = TOML.OffsetDateTime(x)

given Conversion[Seq[TOML], TOML.Array] with
  override def apply(x: Seq[TOML]): TOML.Array = TOML.Array(x)

given Conversion[(String, String), TOML.Map] with
  override def apply(t: (String, String)): TOML.Map =
    val (k, v) = t
    TOML.Map(Map(k -> TOML.String(v)))
