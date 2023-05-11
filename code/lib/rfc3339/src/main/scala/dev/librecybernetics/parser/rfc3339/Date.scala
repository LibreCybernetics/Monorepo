package dev.librecybernetics.parser.rfc3339

import scala.language.postfixOps
import java.time.LocalDate
import java.time.Year

import cats.implicits.*
import cats.parse.Parser
import cats.data.Validated
import cats.syntax.ValidatedIdSyntax

import dev.librecybernetics.parser.*

inline private[parser] def days(days: Int): Parser[Int] =
  intIn(1 to days, "date-mday")

/** Internet Date Format as defined on RFC3339§5.6
  *
  * ABNF: full-date = date-fullyear "-" date-month "-" date-mday
  *
  * Restrictions of date-mday as defined on RFC3339§5.7
  */
private[parser] val date: Parser[LocalDate] =
  (
    // Year
    digit.repExactly(4).string.map(_.toInt),
    dash,
    // Month
    intIn(1 to 12, "date-month"),
    dash
  )
    .mapN((year, _, month, _) => (year, month))
    // Day (varies by year-month as per RFC3339§5.7)
    .flatMap {
      // February
      case (year, 2) if Year.isLeap(year) =>
        days(29).map((year, 2, _))
      case (year, 2)                      =>
        days(29).map((year, 2, _))

      // Non-february
      case (year, month @ (4 | 6 | 9 | 11))              =>
        days(30).map((year, month, _))
      case (year, month @ (1 | 3 | 5 | 7 | 8 | 10 | 12)) =>
        days(31).map((year, month, _))
    }
    .map(LocalDate.of(_, _, _))
    .withContext("full-date")
