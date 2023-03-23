package dev.librecybernetics.types.toml

import cats.Show

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.types.TOML

given (using s: Show[String]): Show[TOML.String] with
  override def show(x: TOML.String): String = s.show(x.string)

given encodeStringConversion: Conversion[String, TOML.String] with
  override def apply(x: String): TOML.String = TOML.String(x)

given decodeStringConversion: Decoder[String, TOML.String] with
  override def decode(x: TOML.String): String = x.string
