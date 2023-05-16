package dev.librecybernetics.types.toml

import cats.{ApplicativeError, Show}

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.types.TOML

given (using s: Show[BigInt]): Show[TOML.Integer] with
  override def show(x: TOML.Integer): String = s.show(x.bigInt)

// Byte

given encodeByteConversion: Conversion[Byte, TOML.Integer] with
  override def apply(x: Byte): TOML.Integer = TOML.Integer(BigInt(x))

given decodeByteFromInteger: Decoder[Byte, TOML.Integer] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      x: TOML.Integer
  ): F[Byte] = ApplicativeError.apply.pure(x.bigInt.toByte)

given decodeByteFromTOML: Decoder[Byte, TOML] with
  override def decode[
      F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]
  ](
      toml: TOML
  ): F[Byte] =
    toml match
      case i: TOML.Integer => decodeByteFromInteger.decode(i)
      case _               =>
        ApplicativeError[F, Set[Decoder.Error]]
          .raiseError(Set(Decoder.Error.InvalidType(toml.getClass.getSimpleName)))

// Short

given encodeShortConversion: Conversion[Short, TOML.Integer] with
  override def apply(x: Short): TOML.Integer = TOML.Integer(BigInt(x))

given decodeShortFromInteger: Decoder[Short, TOML.Integer] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      x: TOML.Integer
  ): F[Short] = ApplicativeError.apply.pure(x.bigInt.toShort)

given decodeShortFromTOML: Decoder[Short, TOML] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      toml: TOML
  ): F[Short] =
    toml match
      case i: TOML.Integer => decodeShortFromInteger.decode(i)
      case _               =>
        ApplicativeError[F, Set[Decoder.Error]]
          .raiseError(Set(Decoder.Error.InvalidType(toml.getClass.getSimpleName)))

// Int

given encodeIntConversion: Conversion[Int, TOML.Integer] with
  override def apply(x: Int): TOML.Integer = TOML.Integer(BigInt(x))

given decodeIntFromInteger: Decoder[Int, TOML.Integer] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      x: TOML.Integer
  ): F[Int] = ApplicativeError.apply.pure(x.bigInt.toInt)

given decodeIntFromTOML: Decoder[Int, TOML] with
  override def decode[
      F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]
  ](
      toml: TOML
  ): F[Int] =
    toml match
      case i: TOML.Integer => decodeIntFromInteger.decode(i)
      case _               =>
        ApplicativeError[F, Set[Decoder.Error]]
          .raiseError(Set(Decoder.Error.InvalidType(toml.getClass.getSimpleName)))

// Long

given encodeLongConversion: Conversion[Long, TOML.Integer] with
  override def apply(x: Long): TOML.Integer = TOML.Integer(BigInt(x))

given decodeLongFromInteger: Decoder[Long, TOML.Integer] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      x: TOML.Integer
  ): F[Long] = ApplicativeError.apply.pure(x.bigInt.toLong)

given decodeLongFromTOML: Decoder[Long, TOML] with
  override def decode[
      F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]
  ](
      toml: TOML
  ): F[Long] =
    toml match
      case i: TOML.Integer => decodeLongFromInteger.decode(i)
      case _               =>
        ApplicativeError[F, Set[Decoder.Error]]
          .raiseError(Set(Decoder.Error.InvalidType(toml.getClass.getSimpleName)))

// BigInt

given encodeBigIntConversion: Conversion[BigInt, TOML.Integer] with
  override def apply(x: BigInt): TOML.Integer = TOML.Integer(x)

given decodeBigIntFromInteger: Decoder[BigInt, TOML.Integer] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      x: TOML.Integer
  ): F[BigInt] = ApplicativeError.apply.pure(x.bigInt)

given decodeBigIntFromTOML: Decoder[BigInt, TOML] with
  override def decode[
      F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]
  ](
      toml: TOML
  ): F[BigInt] =
    toml match
      case i: TOML.Integer => decodeBigIntFromInteger.decode(i)
      case _               =>
        ApplicativeError[F, Set[Decoder.Error]]
          .raiseError(Set(Decoder.Error.InvalidType(toml.getClass.getSimpleName)))
