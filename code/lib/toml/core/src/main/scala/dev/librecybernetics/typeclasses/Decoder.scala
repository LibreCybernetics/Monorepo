package dev.librecybernetics.typeclasses

import scala.Conversion

import cats.ApplicativeError
import cats.data.Validated
import cats.implicits.*

object Decoder:
  enum Error:
    case MissingField(name: String)
    case InvalidType(className: String)

trait Decoder[+T, -E] extends Conversion[E, T]:
  def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](input: E): F[T]

  override def apply(input: E): T =
    decode[Either[Set[Decoder.Error], _]](input).toOption.get
