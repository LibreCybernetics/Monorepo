package dev.librecybernetics.types

import scalanative.unsigned.*

import cats.{Eq, Show}

opaque type UnsignedByte = UByte

object UnsignedByte:
  def apply(b: Byte): UnsignedByte = b.toUByte

given unsignedByteEq: Eq[UnsignedByte] with
  override def eqv(x: UnsignedByte, y: UnsignedByte): Boolean =
    x == y
  override def neqv(x: UnsignedByte, y: UnsignedByte): Boolean =
    x != y

given unsignedByteShow: Show[UnsignedByte] with
  override def show(ub: UnsignedByte): String =
    ub.toString()

extension (ub: UnsignedByte)
  def toByte: Byte = ub.toByte
  def toShort: Short = ub.toShort
  def toInt: Int = ub.toInt
  def toLong: Long = ub.toLong

  def show: String = Show(using unsignedByteShow).show(ub)