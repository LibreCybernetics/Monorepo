package dev.librecybernetics.parser.rfc3339

import scala.language.postfixOps
import java.time.LocalDate

import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*

private def leapYear(year: Int): Boolean =
  year match
    case _ if (year % 400 == 0) => true
    case _ if (year % 100 == 0) => false
    case _ if (year % 4 == 0)   => true

/** full-date = date-fullyear "-" date-month "-" date-mday
  */
val date: Parser[LocalDate] =
  (
    digit.rep(4, 4).string.map(_.toInt).withContext("date-fullyear"),
    dash,
    intIn((1 to 12), "date-month"),
    dash,
    intIn((1 to 31), "date-mday")
  ).mapN((year, _, month, _, day) => (year, month, day))
    .flatMap { case (year, month, day) =>
      month match
        case 2 =>
          day match
            case 30 | 31               =>
              Parser.failWith(show"$year-2-$day is not a valid gregorian calendar date")
            case 29 if !leapYear(year) =>
              Parser.failWith(show"$year-2-$day is not a valid gregorian calendar date on non-leap years")
            case _                     =>
              Parser.pure((year, 2, day))
          end match

        case 4 | 6 | 9 | 11 =>
          day match
            case 31 =>
              Parser.failWith(show"$year-$month-$day is not a valid gregorian calendar date")
            case _  => Parser.pure((year, month, day))

        case _ => Parser.pure((year, month, day))
    }
    .map(LocalDate.of(_, _, _))
