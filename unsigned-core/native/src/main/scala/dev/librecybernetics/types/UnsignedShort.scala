package dev.librecybernetics.types

import scalanative.unsigned.*

import cats.implicits.showInterpolator
import cats.{Eq, MonadError, Show}

opaque type UnsignedShort = UShort

object UnsignedShort:
  given unsignedShortEq: Eq[UnsignedShort] with
    override def eqv(x: UnsignedShort, y: UnsignedShort): Boolean =
      x == y
    override def neqv(x: UnsignedShort, y: UnsignedShort): Boolean =
      x != y

  given unsignedShortShow: Show[UnsignedShort] with
    override def show(us: UnsignedShort): String =
      us.toString()

  extension (b: Byte)
    def toUnsignedShort: UnsignedShort = b.toUShort

  extension (s: Short)
    def toUnsignedShort: UnsignedShort = s.toUShort

  extension (i: Int)
    def toUnsignedShort[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedShort] =
      if (i >= 0 && i < 65536) merr.pure(i.toShort.toUShort)
      else merr.raiseError(IllegalArgumentException(show"Given value $i doesn't satisfy 0 <= $i < 65536"))

  extension (l: Long)
    def toUnsignedShort[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedShort] =
      if (l >= 0 && l < 65536) merr.pure(l.toShort.toUShort)
      else merr.raiseError(IllegalArgumentException(show"Given value $l doesn't satisfy 0 <= $l < 65536"))

  extension (us: UnsignedShort)
    def toByte: Byte = us.toByte
    def toShort: Short = us.toShort
    def toInt: Int = us.toInt
    def toLong: Long = us.toLong

    inline def |(ous: UnsignedShort): UnsignedShort = (us | ous).toUShort
    inline def &(ous: UnsignedShort): UnsignedShort = (us & ous).toUShort
    inline def ^(ous: UnsignedShort): UnsignedShort = (us ^ ous).toUShort

    inline def +(ous: UnsignedShort): UnsignedShort = (us + ous).toUShort
    inline def -(ous: UnsignedShort): UnsignedShort = (us - ous).toUShort
    inline def *(ous: UnsignedShort): UnsignedShort = (us * ous).toUShort
    inline def /(ous: UnsignedShort): UnsignedShort = (us / ous).toUShort
    inline def %(ous: UnsignedShort): UnsignedShort = (us % ous).toUShort

    def show: String = Show(using unsignedShortShow).show(us)