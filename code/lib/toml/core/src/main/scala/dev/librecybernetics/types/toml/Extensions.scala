package dev.librecybernetics.types.toml

import scala.annotation.targetName

import cats.data.Validated
import cats.implicits.*

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.typeclasses.Decoder.Error
import dev.librecybernetics.types.TOML

extension (toml: TOML)
  def decode[A, T <: TOML](using d: Decoder[A, T]): Validated[Set[Error], A] =
    Some(toml)
      .collect { case toml: T =>
        d.decode[Validated[Set[Error], _]](toml)
      }
      .getOrElse(Validated.Invalid(Set(Error.InvalidType(toml.getClass.getSimpleName))))

extension (tomlMap: TOML.Map)
  def getField(name: String): Validated[Set[Error], TOML] =
    Validated.fromOption(tomlMap.map.get(name), Set(Error.MissingField(name)))

  def decodeField[A, T <: TOML](name: String)(using d: Decoder[A, T]): Validated[Set[Error], A] =
    getField(name).andThen(_.decode[A, T])
