package dev.librecybernetics.types

import scalanative.unsigned.*

import cats.implicits.showInterpolator
import cats.{Eq, MonadError, Show}

opaque type UnsignedInt = UInt

object UnsignedInt:
  given unsignedIntEq: Eq[UnsignedInt] with
    override def eqv(x: UnsignedInt, y: UnsignedInt): Boolean =
      x == y
    override def neqv(x: UnsignedInt, y: UnsignedInt): Boolean =
      x != y

  given unsignedIntShow: Show[UnsignedInt] with
    override def show(ui: UnsignedInt): String =
      ui.toString()

  extension (b: Byte)
    def toUnsignedInt: UnsignedInt = b.toUInt

  extension (s: Short)
    def toUnsignedInt: UnsignedInt = s.toUInt

  extension (i: Int)
    def toUnsignedInt: UnsignedInt = i.toUInt

  extension (l: Long)
    def toUnsignedInt[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedInt] =
      if (l >= 0L && l < 4294967296L) merr.pure(l.toInt.toUInt)
      else merr.raiseError(IllegalArgumentException(show"Given value $l doesn't satisfy 0 <= $l < 4294967296"))

  extension (ui: UnsignedInt)
    def toByte: Byte = ui.toByte
    def toShort: Short = ui.toShort
    def toInt: Int = ui.toInt
    def toLong: Long = ui.toLong

    inline def |(oui: UnsignedInt): UnsignedInt = (ui | oui)
    inline def &(oui: UnsignedInt): UnsignedInt = (ui & oui)
    inline def ^(oui: UnsignedInt): UnsignedInt = (ui ^ oui)

    inline def +(oui: UnsignedInt): UnsignedInt = (ui + oui)
    inline def -(oui: UnsignedInt): UnsignedInt = (ui - oui)
    inline def *(oui: UnsignedInt): UnsignedInt = (ui * oui)
    inline def /(oui: UnsignedInt): UnsignedInt = (ui / oui)
    inline def %(oui: UnsignedInt): UnsignedInt = (ui % oui)

    def show: String = unsignedIntShow.show(ui)