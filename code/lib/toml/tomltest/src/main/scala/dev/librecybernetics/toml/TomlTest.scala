package dev.librecybernetics.toml

import cats.effect.std.Console
import cats.effect.{IO, IOApp}
import cats.implicits.*
import cats.parse.Parser
import fs2.*
import fs2.io.*
import mouse.all.*

import dev.librecybernetics.fabric.testReader
import dev.librecybernetics.parser.readTOML

// TODO: Move somewhere else
extension [A, B](either: Either[A, B])
  def mapLeft[C](f: A => C): Either[C, B] =
    either match
      case Left(a)  => Left(f(a))
      case Right(b) => Right(b)

object TomlTest extends IOApp.Simple:
  override def run: IO[Unit] =
    for
      inputString <- stdinUtf8[IO](1024)(using IO.asyncForIO).compile.string
      toml        <- (inputString |> readTOML[Either[Parser.Error, *]])
                       .mapLeft(err => new Exception(show"$err")) |>
                       IO.fromEither

      _ <- Console[IO].println(testReader.read(toml).toString)
    yield ()
