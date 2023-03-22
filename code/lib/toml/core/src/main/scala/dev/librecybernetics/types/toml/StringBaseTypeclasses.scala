package dev.librecybernetics.types.toml

import cats.Show

import dev.librecybernetics.typeclasses.{Decoder, fromConversion}
import dev.librecybernetics.types.TOML

given (using s: Show[String]): Show[TOML.String] with
  override def show(x: TOML.String): String = s.show(x.string)

given encodeStringConversion: Conversion[String, TOML.String] with
  override def apply(x: String): TOML.String = TOML.String(x)

given decodeStringConversion: Conversion[TOML.String, String] with
  override def apply(x: TOML.String): String = x.string

given Decoder[String, TOML.String] = fromConversion