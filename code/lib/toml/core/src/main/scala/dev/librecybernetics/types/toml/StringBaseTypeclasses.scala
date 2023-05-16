package dev.librecybernetics.types.toml

import cats.{ApplicativeError, Show}

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.types.TOML

given (using s: Show[String]): Show[TOML.String] with
  override def show(x: TOML.String): String = s.show(x.string)

given encodeStringConversion: Conversion[String, TOML.String] with
  override def apply(x: String): TOML.String = TOML.String(x)

given decodeStringFromString: Decoder[String, TOML.String] with
  override def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](
      s: TOML.String
  ): F[String] = ApplicativeError.apply.pure(s.string)

given decodeStringFromTOML: Decoder[String, TOML] with
  override def decode[
      F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]
  ](
      toml: TOML
  ): F[String] = toml match
    case s: TOML.String => decodeStringFromString.decode(s)
    case _              =>
      ApplicativeError[F, Set[Decoder.Error]]
        .raiseError(Set(Decoder.Error.InvalidType(toml.getClass.getSimpleName)))
