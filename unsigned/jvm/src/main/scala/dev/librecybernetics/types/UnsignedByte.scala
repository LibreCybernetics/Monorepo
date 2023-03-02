package dev.librecybernetics.types

opaque type UnsignedByte = Byte

object UnsignedByte:
  def apply(b: Byte): UnsignedByte = b

extension (ub: UnsignedByte)
  def toString: String = ub match {
    case _ if (ub & 0x80) == 0 => ub.toString
    case _ => ((ub & 0x7F.toByte) | 0x0080).toString
  }