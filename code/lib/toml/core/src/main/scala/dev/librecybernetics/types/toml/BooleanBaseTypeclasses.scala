package dev.librecybernetics.types.toml

import cats.Show

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.types.TOML

given (using s: Show[Boolean]): Show[TOML.Boolean] with
  override def show(x: TOML.Boolean): String = s.show(x.boolean)

given encodeBooleanConversion: Conversion[Boolean, TOML.Boolean] with
  override def apply(x: Boolean): TOML.Boolean = TOML.Boolean(x)

given decodeBooleanConversion: Decoder[Boolean, TOML.Boolean] with
  override def decode(x: TOML.Boolean): Boolean = x.boolean
