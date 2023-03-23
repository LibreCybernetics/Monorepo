package dev.librecybernetics.typeclasses

import scala.Conversion

trait Decoder[+T, -E] extends Conversion[E, T]{
  def decode(input: E): T

  override def apply(input: E): T = decode(input)
}
