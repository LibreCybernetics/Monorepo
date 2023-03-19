package dev.librecybernetics.parser.rfc3339

import java.time.{OffsetDateTime, ZoneOffset}

import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*

class OffsetDateTimeSpec extends AnyWordSpec {
  "OffsetDateTime" when {
    "valid" should {
      Map(
        "1979-05-27T07:32:00Z"          ->
          OffsetDateTime.of(1979, 5, 27, 7, 32, 0, 0, ZoneOffset.ofHoursMinutes(0, 0)),
        "1979-05-27 07:32:00Z"          ->
          OffsetDateTime.of(1979, 5, 27, 7, 32, 0, 0, ZoneOffset.ofHoursMinutes(0, 0)),
        "1979-05-27T00:32:00-07:00"     ->
          OffsetDateTime.of(1979, 5, 27, 0, 32, 0, 0, ZoneOffset.ofHoursMinutes(-7, 0)),
        "2023-03-18T12:34:56.123+05:30" ->
          OffsetDateTime.of(2023, 3, 18, 12, 34, 56, 123_000_000, ZoneOffset.ofHoursMinutes(5, 30))
      ) foreach { (s, odt) =>
        s in genericSuccess(offsetDateTime)(s, odt)
      }
    }
  }
}
