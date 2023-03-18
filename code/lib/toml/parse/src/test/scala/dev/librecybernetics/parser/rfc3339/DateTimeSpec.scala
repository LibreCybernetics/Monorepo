package dev.librecybernetics.parser.rfc3339

import java.time.LocalDateTime

import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*

class DateTimeSpec extends AnyWordSpec {
  "DateTime" when {
    "Valid" should {
      Map(
        "2023-03-18T12:34:56" -> LocalDateTime.of(2023, 3, 18, 12, 34, 56),
        "1990-01-01T00:00:00" -> LocalDateTime.of(1990, 1, 1, 0, 0, 0)
      ) foreach { (s, ldt) =>
        s in genericSuccess(dateTime)(s, ldt)
      }
    }
  }
}
