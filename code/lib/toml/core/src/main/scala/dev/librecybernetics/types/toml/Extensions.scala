package dev.librecybernetics.types.toml

import cats.data.Validated
import cats.implicits.*

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.typeclasses.Decoder.Error
import dev.librecybernetics.types.TOML

extension (toml: TOML)
  def mapValue[A](pf: PartialFunction[TOML, A]): Validated[Set[Error], A] =
    Validated.fromOption(pf.lift(toml), Set(Error.InvalidType(toml.getClass.getSimpleName)))

  def decodeAny[A](using d: Decoder[A, TOML]): Validated[Set[Error], A] =
    d.decode[Validated[Set[Error], _]](toml)

  def decodeString[A](using d: Decoder[A, TOML.String]): Validated[Set[Error], A] =
    Some(toml)
      .collect { case toml: TOML.String =>
        d.decode[Validated[Set[Error], _]](toml)
      }
      .getOrElse(Validated.Invalid(Set(Error.InvalidType(toml.getClass.getSimpleName))))

  def decodeMap[A](using d: Decoder[A, TOML.Map]): Validated[Set[Error], A] =
    Some(toml)
      .collect { case toml: TOML.Map =>
        d.decode[Validated[Set[Error], _]](toml)
      }
      .getOrElse(Validated.Invalid(Set(Error.InvalidType(toml.getClass.getSimpleName))))

extension (tomlMap: TOML.Map)
  def getField(name: String): Validated[Set[Error], TOML] =
    Validated.fromOption(tomlMap.map.get(name), Set(Error.MissingField(name)))
