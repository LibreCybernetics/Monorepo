package dev.librecybernetics.parser

import scala.annotation.unused

import cats.MonadError
import cats.parse.Parser.Error

import dev.librecybernetics.types.TOML

// TODO: Change `F[_]: [F[_]] =>> MonadError[F, Error]` to `F[_]: MonadError[_, Error]` or similar
@unused
def readTOML[F[_]: [F[_]] =>> MonadError[F, Error]](
                                                     input: String
): F[TOML.Map] =
  toml.Toml.toml.parse(input) match
    case Left(err)        => MonadError.apply.raiseError(err)
    case Right((_, toml)) => MonadError.apply.pure(toml)
