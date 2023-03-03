package dev.librecybernetics.types

import cats.{Eq, Show}

opaque type UnsignedByte = Byte

object UnsignedByte:
  def apply(b: Byte): UnsignedByte = b

given unsignedByteEq: Eq[UnsignedByte] with
  override def eqv(x: UnsignedByte, y: UnsignedByte): Boolean =
    x == y
  override def neqv(x: UnsignedByte, y: UnsignedByte): Boolean =
    x != y

given unsignedByteShow: Show[UnsignedByte] with
  override def show(ub: UnsignedByte): String =
    ub match {
      case _ if (ub & 0x80) == 0 => ub.toString
      case _ => ((ub & 0x7F.toByte) | 0x0080).toString
    }

extension (ub: UnsignedByte)
  def toByte: Byte = ub
  def toShort: Short = ub.toShort
  def toInt: Int = ub.toInt
  def toLong: Long = ub.toLong
  def show: String = Show(using unsignedByteShow).show(ub)