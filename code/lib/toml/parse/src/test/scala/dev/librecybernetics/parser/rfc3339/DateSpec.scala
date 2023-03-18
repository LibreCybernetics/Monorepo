package dev.librecybernetics.parser.rfc3339

import java.time.LocalDate

import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*

class DateSpec extends AnyWordSpec {
  "Date" when {
    "Valid" should {
      Map(
        "2023-03-18" -> LocalDate.of(2023, 3, 18),
        "1990-01-01" -> LocalDate.of(1990, 1, 1),
        "2000-02-29" -> LocalDate.of(2000, 2, 29),
        "2021-12-31" -> LocalDate.of(2021, 12, 31)
      ) foreach { (s, ld) =>
        s in genericSuccess(date)(s, ld)
      }
    }

    "Invalid" should {
      Map(
      "2023-03-32" -> "must fail: 32 out of range(Range 1 to 31) for date-mday",
      "2023-00-18" -> "must fail: 0 out of range(Range 1 to 12) for date-month",
      "2023-13-18" -> "must fail: 13 out of range(Range 1 to 12) for date-month",
      "2023-02-30" -> "must fail: 2023-2-30 is not a valid gregorian calendar date",
      "2023-2-18" -> "must be a char within the range of: ['0', '9']",
      "2023-03-8" -> "must be a char within the range of: ['0', '9']"
      ) foreach { (s, m) =>
        s in genericFailure(date)(s, m)
      }
    }
  }
}
