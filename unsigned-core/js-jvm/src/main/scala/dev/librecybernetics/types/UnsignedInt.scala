package dev.librecybernetics.types

import cats.implicits.showInterpolator
import cats.{Eq, MonadError, Show}

opaque type UnsignedInt = Int

object UnsignedInt:
  private def unsignedInt2Long(b: UnsignedInt): Long =
    b match
      case _ if (b & 0x80000000) == 0 => b
      case _ => (b & 0x7FFFFFFF) | 0x0000000080000000L

  given unsignedIntEq: Eq[UnsignedInt] with
    override def eqv(x: UnsignedInt, y: UnsignedInt): Boolean =
      x == y
    override def neqv(x: UnsignedInt, y: UnsignedInt): Boolean =
      x != y

  given unsignedIntShow: Show[UnsignedInt] with
    override def show(us: UnsignedInt): String =
      unsignedInt2Long(us).toString

  extension (b: Byte)
    def toUnsignedInt: UnsignedInt = b.toInt

  extension (s: Short)
    def toUnsignedInt: UnsignedInt = s.toInt

  extension (i: Int)
    def toUnsignedInt: UnsignedInt = i

  extension (l: Long)
    def toUnsignedInt[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedInt] =
      if (l >= 0L && l < 4294967296L) merr.pure(l.toInt)
      else merr.raiseError(IllegalArgumentException(show"Given value $l doesn't satisfy 0 <= $l < 4294967296"))

  extension (us: UnsignedInt)
    def toByte: Byte = us.toByte
    def toShort: Short = us.toShort
    def toInt: Int = us
    def toLong: Long = unsignedInt2Long(us)

    inline def |(ous: UnsignedInt): UnsignedInt = (us | ous).toInt
    inline def &(ous: UnsignedInt): UnsignedInt = (us & ous).toInt
    inline def ^(ous: UnsignedInt): UnsignedInt = (us ^ ous).toInt

    inline def +(ous: UnsignedInt): UnsignedInt = (unsignedInt2Long(us) + unsignedInt2Long(ous)).toInt
    inline def -(ous: UnsignedInt): UnsignedInt = (unsignedInt2Long(us) - unsignedInt2Long(ous)).toInt
    inline def *(ous: UnsignedInt): UnsignedInt = (unsignedInt2Long(us) * unsignedInt2Long(ous)).toInt
    inline def /(ous: UnsignedInt): UnsignedInt = (unsignedInt2Long(us) / unsignedInt2Long(ous)).toInt
    inline def %(ous: UnsignedInt): UnsignedInt = (unsignedInt2Long(us) % unsignedInt2Long(ous)).toInt

    def show: String = unsignedIntShow.show(us)
