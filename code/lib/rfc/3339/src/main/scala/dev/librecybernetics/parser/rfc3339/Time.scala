package dev.librecybernetics.parser.rfc3339

import java.time.LocalTime

import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*

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

extension (p: Parser[((Int, Int, Int), Option[Int])])
  private def validateTime: Parser[((Int, Int, Int), Option[Int])] =
    p.flatMap {
      // TODO: Better leap second handling; currently smearing
      case ((23, 59, 60), n)          => Parser.pure(((23, 59, 59), Some(999_999_999)))
      case ((hour, minute, 60), nano) =>
        val nanoFolded = nano
          .map(_.toString)
          .map(s => ":" + "0".repeat(9 - s.length) + s)
          .getOrElse("")
        Parser.failWith(
          show"Leap seconds are only allowed as 23:59:60 but got $hour:$minute:60$nanoFolded"
        )

      case t => Parser.pure(t)
    }

/** time-whole = time-hour ":" time-minute ":" time-second (* non-spec)
  *
  * partial-time = time-whole [time-secfrac]
  */
private[parser] val time: Parser[LocalTime] =
  (wholeTime ~ fracTime.backtrack.?).validateTime
    .map { case ((hour, minute, second), nano) =>
      LocalTime.of(hour, minute, second, nano.getOrElse(0))
    }
    .withContext("partial-time")
