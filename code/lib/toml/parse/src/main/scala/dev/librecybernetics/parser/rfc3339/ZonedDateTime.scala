package dev.librecybernetics.parser.rfc3339

import java.time.{ZoneOffset, OffsetDateTime}

import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.base.*

/** TOMLv1 specifies rfc3339 date format Section 5.6 specifies:
  *
  * ```
  * full-time       = partial-time time-offset
  *
  * time-numoffset  = ("+" / "-") time-hour ":" time-minute
  * time-offset     = "Z" / time-numoffset
  * ```
  */
private val timeOffset: Parser[(Sign, Byte, Byte)] =
  (
    sign,
    digit.rep(2).string.map(_.toByte),
    colon,
    digit.rep(2).string.map(_.toByte)
  ).mapN { case (s, d1, (), d2) => (s, d1, d2) }

private def transformSign(s: Sign, b: Byte): Int =
  s match
    case Sign.Minus => -b
    case Sign.Plus  => b

/** full-time = partial-time time-offset
  *
  * full-date "T" full-time
  */
private[parser] val offsetDateTime: Parser[OffsetDateTime] =
  (dateTime ~ (Parser.char('Z').map(Left(_)).backtrack | timeOffset.map(Right(_))))
    .map {
      case (dateTime, Left(()))                   =>
        OffsetDateTime.of(dateTime, ZoneOffset.UTC)
      case (dateTime, Right((s, hours, minutes))) =>
        OffsetDateTime.of(
          dateTime,
          ZoneOffset.ofHoursMinutes(
            transformSign(s, hours),
            transformSign(s, minutes)
          )
        )
    }
