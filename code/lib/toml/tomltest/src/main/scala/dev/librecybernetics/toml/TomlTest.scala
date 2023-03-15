package dev.librecybernetics.toml

import cats.effect.{IO, IOApp}
import cats.effect.std.Console
import cats.implicits.*
import mouse.all.*

import fs2.io.*
import fs2.*

import dev.librecybernetics.fabric.testReader
import dev.librecybernetics.parser.toml.Toml

// TODO: Move somewhere else
extension [A, B](either: Either[A, B])
  def mapLeft[C](f: A => C): Either[C, B] =
    either match
      case Left(a)  => Left(f(a))
      case Right(b) => Right(b)

object TomlTest extends IOApp.Simple:
  override def run =
    for
      inputString <- stdinUtf8[IO](1024).compile.string

      parsed <- (inputString |> Toml.toml.parse)
                  .mapLeft(err => new Exception(show"$err")) |>
                  IO.fromEither

      (_, toml) = parsed
      _        <- Console[IO].println(testReader.read(toml).toString)
    yield ()
