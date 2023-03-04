package dev.librecybernetics.types

import cats.implicits.showInterpolator
import cats.{Eq, MonadError, Show}

opaque type UnsignedShort = Short

object UnsignedShort:
  private def unsignedShort2Int(b: UnsignedShort): Int =
    b match
      case _ if (b & 0x8000) == 0 => b
      case _ => (b & 0x7FFF.toShort) | 0x00008000

  given unsignedShortEq: Eq[UnsignedShort] with
    override def eqv(x: UnsignedShort, y: UnsignedShort): Boolean =
      x == y
    override def neqv(x: UnsignedShort, y: UnsignedShort): Boolean =
      x != y

  given unsignedShortShow: Show[UnsignedShort] with
    override def show(us: UnsignedShort): String =
      unsignedShort2Int(us).toString

  extension (b: Byte)
    def toUnsignedShort: UnsignedShort = b.toShort

  extension (s: Short)
    def toUnsignedShort: UnsignedShort = s

  extension (i: Int)
    def toUnsignedShort[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedShort] =
      if (i >= 0 && i < 65536) merr.pure(i.toShort)
      else merr.raiseError(IllegalArgumentException(show"Given value $i doesn't satisfy 0 <= $i < 65536"))

  extension (l: Long)
    def toUnsignedShort[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedShort] =
      if (l >= 0 && l < 65536) merr.pure(l.toShort)
      else merr.raiseError(IllegalArgumentException(show"Given value $l doesn't satisfy 0 <= $l < 65536"))

  extension (us: UnsignedShort)
    def toByte: Byte = us.toByte
    def toShort: Short = us
    def toInt: Int = unsignedShort2Int(us)
    def toLong: Long = unsignedShort2Int(us).toLong

    inline def |(ous: UnsignedShort): UnsignedShort = (us | ous).toShort
    inline def &(ous: UnsignedShort): UnsignedShort = (us & ous).toShort
    inline def ^(ous: UnsignedShort): UnsignedShort = (us ^ ous).toShort

    inline def +(ous: UnsignedShort): UnsignedShort = (unsignedShort2Int(us) + unsignedShort2Int(ous)).toShort
    inline def -(ous: UnsignedShort): UnsignedShort = (unsignedShort2Int(us) - unsignedShort2Int(ous)).toShort
    inline def *(ous: UnsignedShort): UnsignedShort = (unsignedShort2Int(us) * unsignedShort2Int(ous)).toShort
    inline def /(ous: UnsignedShort): UnsignedShort = (unsignedShort2Int(us) / unsignedShort2Int(ous)).toShort
    inline def %(ous: UnsignedShort): UnsignedShort = (unsignedShort2Int(us) % unsignedShort2Int(ous)).toShort

    def show: String = unsignedShortShow.show(us)
