package dev.librecybernetics.types

import java.time.Instant

import cats.syntax.all.*
import cats.data.{Validated, ValidatedNec}
import cats.effect.kernel.{Async, Clock}
import cats.effect.std.SecureRandom

/** UUID version 7 features a time-ordered value field derived from the widely implemented and well known Unix Epoch
  * timestamp source, the number of milliseconds seconds since midnight 1 Jan 1970 UTC, leap seconds excluded. As well
  * as improved entropy characteristics over versions 1 or 6.
  *
  * Spec: https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html
  */
case class UUIDv7(
    timestamp: java.time.Instant,
    nonce: Array[Byte]
) {
  require(nonce.length == 10, "Nonce must be 10 bytes long")
  require(UUIDv7.checkLastByte(nonce.last), "Nonce must end with 0b00_0000")
}

object UUIDv7:
  enum Error:
    case InvalidNonce

  private[UUIDv7] def checkLastByte(byte: Byte): Boolean =
    (byte & 0x3f.toByte) == 0

  def apply(
      timestamp: java.time.Instant,
      nonce: Array[Byte]
  ): Validated[Error, UUIDv7] =
    println(nonce.last)
    Validated
      .cond(
        nonce.length == 10 && UUIDv7.checkLastByte(nonce.last),
        nonce,
        Error.InvalidNonce
      )
      .map(new UUIDv7(timestamp, _))

  def gen[F[_]]()(using
      async: Async[F],
      clock: Clock[F],
      secureRandom: SecureRandom[F]
  ): F[UUIDv7] = for
    instant <- clock.realTime
                 .map(c => Instant.EPOCH.plusMillis(c.toMillis))
    nonce   <- secureRandom.nextBytes(10)
    _        = nonce.update(9, (nonce.last & 0xc0.toByte).toByte)
  yield UUIDv7(instant, nonce).toOption.get
end UUIDv7
