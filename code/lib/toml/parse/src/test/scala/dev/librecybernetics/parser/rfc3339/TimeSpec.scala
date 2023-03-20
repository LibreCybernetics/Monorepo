package dev.librecybernetics.parser.rfc3339

import java.time.LocalTime

import cats.data.NonEmptyList
import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*

class TimeSpec extends AnyWordSpec {
  "Time" when {
    "Valid" should {
      Map(
        "12:34:56"        -> LocalTime.of(12, 34, 56),
        "00:00:00"        -> LocalTime.of(0, 0, 0),
        "23:59:59"        -> LocalTime.of(23, 59, 59),
        "23:59:60"        -> LocalTime.MAX, // TODO: Better Leap Second
        "12:34:56.789"    -> LocalTime.of(12, 34, 56, 789_000_000),
        "12:34:56.000123" -> LocalTime.of(12, 34, 56, 123_000)
      ) foreach { (s, lt) =>
        s in genericSuccess(time)(s, lt)
      }
    }

    "Invalid" should {
      Map(
        "25:12:34" -> "context: partial-time, must fail: 25 out of range(Range 0 to 23) for time-hour",
        "12:60:34" -> "context: partial-time, must fail: 60 out of range(Range 0 to 59) for time-minute",
        "12:34:60" -> "context: partial-time, must fail: Leap seconds are only allowed as 23:59:60 but got 12:34:60"
      ) foreach { (s, message) =>
        s in genericFailure(time)(s, message)
      }
    }
  }
}
