package dev.librecybernetics.typeclasses

import scala.Conversion

import cats.ApplicativeError
import cats.implicits.*

object Decoder:
  enum Error:
    case InvalidInput(input: String)
    case InvalidType(input: String, expected: String)

trait Decoder[+T, -E] extends Conversion[E, T]:
  def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](input: E): F[T]

  override def apply(input: E): T =
    decode[Either[Set[Decoder.Error], _]](input).toOption.get
