package dev.librecybernetics.typeclasses

import scala.Conversion

trait Decoder[+T, -E] {
  def decode(input: E): T

  def apply(input: E): T = decode(input)
}

given fromConversion[T, E](using c: Conversion[E, T]): Decoder[T, E] with
  override def decode(input: E): T = c(input)
