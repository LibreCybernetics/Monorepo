package dev.librecybernetics.types.toml

import cats.Show

import dev.librecybernetics.types.TOML
import dev.librecybernetics.typeclasses.Decoder

given (using s: Show[Double]): Show[TOML.Float] with
  override def show(t: TOML.Float): String = s.show(t.double)

given encodeFloatConversion: Conversion[Float, TOML.Float] with
  override def apply(x: Float): TOML.Float = TOML.Float(x.toDouble)

given decodeFloatConversion: Decoder[Float, TOML.Float] with
  override def decode(x: TOML.Float): Float = x.double.toFloat


given encodeDoubleConversion: Conversion[Double, TOML.Float] with
  override def apply(x: Double): TOML.Float = TOML.Float(x)

given decodeDoubleConversion: Decoder[Double, TOML.Float] with
  override def decode(x: TOML.Float): Double = x.double


given encodeBigDecimalConversion: Conversion[BigDecimal, TOML.Float] with
  override def apply(x: BigDecimal): TOML.Float = TOML.Float(x.toDouble)

given decodeBigDecimalConversion: Decoder[BigDecimal, TOML.Float] with
  override def decode(x: TOML.Float): BigDecimal = BigDecimal(x.double)

