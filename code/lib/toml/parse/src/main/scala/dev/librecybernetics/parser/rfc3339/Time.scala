package dev.librecybernetics.parser.rfc3339

import scala.language.postfixOps
import java.time.LocalTime

import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

def intIn(range: Range, context: String): Parser[Int] =
  digit.rep(2).string.map(_.toInt).flatMap {
    case i if range contains i =>
      Parser.pure(i)
    case i                     =>
      Parser.failWith(show"$i out of range(${range.toString}) for $context")
  }

/** time-hour ":" time-minute ":" time-second
  */
private val wholeTime: Parser[(Int, Int, Int)] =
  (
    intIn((0 to 23), "time-hour"),
    colon,
    intIn((0 to 59), "time-minute"),
    colon,
    intIn((0 to 60), "time-second"),
  ).mapN((hour, _, minute, _, second) => (hour, minute, second))

/** time-secfrac
  */
private val fracTime: Parser[Int] =
  dot *> digit
    .rep(1, 9)
    .string
    .map(s => s + "0".repeat(9 - s.length))
    .map(_.toInt)
    .withContext("time-secfrac")

val time: Parser[LocalTime] =
  (wholeTime ~ fracTime.backtrack.?)
    .withContext("partial-time")
    .flatMap {
      // TODO: Better leap second handling
      case ((23, 59, 60), _)              =>
        Parser.pure(LocalTime.MAX)
      case ((hour, minute, 60), nano)     =>
        val nanoFolded = nano
          .map(_.toString)
          .map(s => ":" + "0".repeat(9 - s.length) + s)
          .getOrElse("")
        Parser.failWith(
          show"Leap seconds are only allowed as 23:59:60 but got $hour:$minute:60$nanoFolded"
        )
      case ((hour, minute, second), nano) =>
        Parser.pure(LocalTime.of(hour, minute, second, nano.getOrElse(0)))
    }
