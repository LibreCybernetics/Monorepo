package dev.librecybernetics.types

import scalanative.unsigned.*

import cats.implicits.showInterpolator
import cats.{Eq, MonadError, Show}

opaque type UnsignedByte = UByte

given unsignedByteEq: Eq[UnsignedByte] with
  override def eqv(x: UnsignedByte, y: UnsignedByte): Boolean =
    x == y
  override def neqv(x: UnsignedByte, y: UnsignedByte): Boolean =
    x != y

given unsignedByteShow: Show[UnsignedByte] with
  override def show(ub: UnsignedByte): String =
    ub.toString()

extension (b: Byte)
  def toUnsignedByte: UnsignedByte = b.toUByte

extension (s: Short)
  def toUnsignedByte[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedByte] = {
    if (s >= 0 && s < 256) merr.pure(s.toByte.toUByte)
    else merr.raiseError(IllegalArgumentException(show"Given value $s doesn't satisfy 0 <= $s < 256"))
  }

extension (i: Int)
  def toUnsignedByte[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedByte] = {
    if (i >= 0 && i < 256) merr.pure(i.toByte.toUByte)
    else merr.raiseError(IllegalArgumentException(show"Given value $i doesn't satisfy 0 <= $i < 256"))
  }

extension (l: Long)
  def toUnsignedByte[F[_]](using merr: MonadError[F, IllegalArgumentException]): F[UnsignedByte] = {
    if (l >= 0 && l < 256) merr.pure(l.toByte.toUByte)
    else merr.raiseError(IllegalArgumentException(show"Given value $l doesn't satisfy 0 <= $l < 256"))
  }

extension (ub: UnsignedByte)
  def toByte: Byte = ub.toByte
  def toShort: Short = ub.toShort
  def toInt: Int = ub.toInt
  def toLong: Long = ub.toLong

  inline def |(oub: UnsignedByte): UnsignedByte = (ub | oub).toUByte
  inline def &(oub: UnsignedByte): UnsignedByte = (ub & oub).toUByte
  inline def ^(oub: UnsignedByte): UnsignedByte = (ub ^ oub).toUByte

  inline def +(oub: UnsignedByte): UnsignedByte = (ub + oub).toUByte
  inline def -(oub: UnsignedByte): UnsignedByte = (ub - oub).toUByte
  inline def *(oub: UnsignedByte): UnsignedByte = (ub * oub).toUByte
  inline def /(oub: UnsignedByte): UnsignedByte = (ub / oub).toUByte
  inline def %(oub: UnsignedByte): UnsignedByte = (ub % oub).toUByte

  def show: String = Show(using unsignedByteShow).show(ub)