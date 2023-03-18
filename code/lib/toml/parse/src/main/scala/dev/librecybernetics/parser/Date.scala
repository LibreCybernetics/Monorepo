package dev.librecybernetics.parser.toml.base

import scala.language.postfixOps
import java.time.LocalDate

import cats.parse.Parser

import dev.librecybernetics.parser.*

/** TOMLv1 specifies rfc3339 date format Section 5.6 specifies:
  *
  * ```
  * full-date = date-fullyear "-" date-month "-" date-mday
  *
  * date-fullyear = 4DIGIT
  * date-month = 2DIGIT ; 01-12
  * date-mday  = 2DIGIT ; 01-28, 01-29, 01-30, 01-31 based on month/year
  * ```
  */
val date: Parser[LocalDate] =
  (for
    year  <- digit.rep(4).string.map(_.toShort)
    _     <- dash
    month <- digit.rep(2).string.map(_.toShort).filter((1 to 12) contains)
    _     <- dash
    day   <- digit.rep(2).string.map(_.toShort) // TODO: Filter
  yield LocalDate.of(year, month, day)).backtrack
