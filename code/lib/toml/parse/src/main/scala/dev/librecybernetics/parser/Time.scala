package dev.librecybernetics.parser.toml.base

import scala.language.postfixOps
import java.time.LocalTime

import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

/** TOMLv1 specifies rfc3339 date format Section 5.6 specifies:
  *
  * ```
  * partial-time    = time-hour ":" time-minute ":" time-second[time-secfrac]
  * full-time       = partial-time time-offset
  *
  * time-hour       = 2DIGIT  ; 00-23
  * time-minute     = 2DIGIT  ; 00-59
  * time-second     = 2DIGIT  ; 00-58, 00-59, 00-60 based on leap second rules
  * time-secfrac    = "." 1*DIGIT
  * time-numoffset  = ("+" / "-") time-hour ":" time-minute
  * time-offset     = "Z" / time-numoffset
  * ```
  */
val time: Parser[LocalTime] =
  (for
    hour   <- digit.rep(2).string.map(_.toShort).filter((1 to 23) contains)
    _      <- colon
    minute <- digit.rep(2).string.map(_.toShort).filter((1 to 59) contains)
    _      <- colon
    second <- digit.rep(2).string.map(_.toShort).filter((1 to 60) contains) // TODO: Better filter
  // TODO: time-secfrac
  yield LocalTime.of(hour, minute, second)).backtrack
