package dev.librecybernetics.types

import cats.implicits.showInterpolator
import cats.{Eq, MonadError, Show}

opaque type UnsignedByte = Byte

object UnsignedByte:
  private def unsignedByte2Short(b: UnsignedByte): Short =
    b match
      case _ if (b & 0x80) == 0 => b
      case _ => ((b & 0x7F.toByte) | 0x0080).toShort

  given unsignedByteEq: Eq[UnsignedByte] with
    override def eqv(x: UnsignedByte, y: UnsignedByte): Boolean =
      x == y
    override def neqv(x: UnsignedByte, y: UnsignedByte): Boolean =
      x != y

  given unsignedByteShow: Show[UnsignedByte] with
    override def show(ub: UnsignedByte): String =
      unsignedByte2Short(ub).toString

  extension (b: Byte)
    def toUnsignedByte: UnsignedByte = b

  extension (s: Short)
    def toUnsignedByte[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedByte] =
      if (s >= 0 && s < 256) merr.pure(s.toByte)
      else merr.raiseError(IllegalArgumentException(show"Given value $s doesn't satisfy 0 <= $s < 256"))

  extension (i: Int)
    def toUnsignedByte[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedByte] =
      if (i >= 0 && i < 256) merr.pure(i.toByte)
      else merr.raiseError(IllegalArgumentException(show"Given value $i doesn't satisfy 0 <= $i < 256"))

  extension (l: Long)
    def toUnsignedByte[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedByte] =
      if (l >= 0 && l < 256) merr.pure(l.toByte)
      else merr.raiseError(IllegalArgumentException(show"Given value $l doesn't satisfy 0 <= $l < 256"))

  extension (ub: UnsignedByte)
    def toByte: Byte = ub
    def toShort: Short = unsignedByte2Short(ub)
    def toInt: Int = unsignedByte2Short(ub).toInt
    def toLong: Long = unsignedByte2Short(ub).toLong

    inline def |(oub: UnsignedByte): UnsignedByte = (ub | oub).toByte
    inline def &(oub: UnsignedByte): UnsignedByte = (ub & oub).toByte
    inline def ^(oub: UnsignedByte): UnsignedByte = (ub ^ oub).toByte

    inline def +(oub: UnsignedByte): UnsignedByte = (unsignedByte2Short(ub) + unsignedByte2Short(oub)).toByte
    inline def -(oub: UnsignedByte): UnsignedByte = (unsignedByte2Short(ub) - unsignedByte2Short(oub)).toByte
    inline def *(oub: UnsignedByte): UnsignedByte = (unsignedByte2Short(ub) * unsignedByte2Short(oub)).toByte
    inline def /(oub: UnsignedByte): UnsignedByte = (unsignedByte2Short(ub) / unsignedByte2Short(oub)).toByte
    inline def %(oub: UnsignedByte): UnsignedByte = (unsignedByte2Short(ub) % unsignedByte2Short(oub)).toByte

    def show: String = unsignedByteShow.show(ub)