package dev.librecybernetics.types.toml

import cats.{ApplicativeError, Show}

import dev.librecybernetics.types.TOML
import dev.librecybernetics.typeclasses.Decoder

given (using s: Show[Double]): Show[TOML.Float] with
  override def show(t: TOML.Float): String = s.show(t.double)

given encodeFloatConversion: Conversion[Float, TOML.Float] with
  override def apply(x: Float): TOML.Float = TOML.Float(x.toDouble)

given decodeFloatConversion: Decoder[Float, TOML.Float] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      x: TOML.Float
  ): F[Float] = ApplicativeError.apply.pure(x.double.toFloat)

given encodeDoubleConversion: Conversion[Double, TOML.Float] with
  override def apply(x: Double): TOML.Float = TOML.Float(x)

given decodeDoubleConversion: Decoder[Double, TOML.Float] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      x: TOML.Float
  ): F[Double] = ApplicativeError.apply.pure(x.double)

given encodeBigDecimalConversion: Conversion[BigDecimal, TOML.Float] with
  override def apply(x: BigDecimal): TOML.Float = TOML.Float(x.toDouble)

given decodeBigDecimalConversion: Decoder[BigDecimal, TOML.Float] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      x: TOML.Float
  ): F[BigDecimal] = ApplicativeError.apply.pure(BigDecimal(x.double))
