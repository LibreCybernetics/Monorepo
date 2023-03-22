package dev.librecybernetics.types.toml

import cats.Show

import dev.librecybernetics.types.TOML
import dev.librecybernetics.typeclasses.{Decoder, fromConversion}

given (using s: Show[Double]): Show[TOML.Float] with
  override def show(t: TOML.Float): String = s.show(t.double)

given encodeFloatConversion: Conversion[Float, TOML.Float] with
  override def apply(x: Float): TOML.Float = TOML.Float(x.toDouble)

given decodeFloatConversion: Conversion[TOML.Float, Float] with
  override def apply(x: TOML.Float): Float = x.double.toFloat

given Decoder[Float, TOML.Float] = fromConversion

given encodeDoubleConversion: Conversion[Double, TOML.Float] with
  override def apply(x: Double): TOML.Float = TOML.Float(x)

given decodeDoubleConversion: Conversion[TOML.Float, Double] with
  override def apply(x: TOML.Float): Double = x.double

given Decoder[Double, TOML.Float] = fromConversion

given encodeBigDecimalConversion: Conversion[BigDecimal, TOML.Float] with
  override def apply(x: BigDecimal): TOML.Float = TOML.Float(x.toDouble)

given decodeBigDecimalConversion: Conversion[TOML.Float, BigDecimal] with
  override def apply(x: TOML.Float): BigDecimal = BigDecimal(x.double)

given Decoder[BigDecimal, TOML.Float] = fromConversion
