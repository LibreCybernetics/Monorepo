package dev.librecybernetics.network

import dev.librecybernetics.types.*
import UnsignedByte.*

opaque type Octet = UnsignedByte

object Octet {
  def apply(b: UnsignedByte): Octet = b


  private def baseConversion(c: Char): Int = c match {
    case _ if c >= 48 && c < 59 => c.toByte - 48
    case _ if c >= 65 && c < 71 => c.toByte - 55
  }

  def fromHexString(s: String): Octet = {
    require(s.length == 2)
    require(s.forall(c => (c >= 48 && c < 59) || (c >= 65 && c < 71)))

    s.map(baseConversion) match {
      case Seq(a, b) => (a * 16 + b).toByte.toUnsignedByte
    }
  }
}

extension (o: Octet)
  private def baseConversion(b: Byte): Char = b match
    case _ if b < 10 => (b + 48).toChar
    case _ if b < 16 => (b + 55).toChar

  // TODO: Dotty Bug? if changed to `inline def &`
  def and(os: Octet): Octet = o & os

  def toHexString: String =
    Seq(
      (o / 16.toByte.toUnsignedByte).toByte,
      (o % 16.toByte.toUnsignedByte).toByte
    ).map(baseConversion) match {
      case Seq(a, b) => s"$a$b"
    }
