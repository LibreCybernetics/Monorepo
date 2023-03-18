package dev.librecybernetics.parser.rfc3339

import java.time.LocalDateTime

import cats.parse.Parser

import dev.librecybernetics.parser.*

/** TOMLv1 specifies rfc3339 date format Section 5.6 specifies:
  *
  * ```
  * date-time       = full-date "T" full-time
  * ```
  */
val dateTime: Parser[LocalDateTime] =
  ((date <* Parser.char('T')) ~ time)
    .map((date, time) => LocalDateTime.of(date, time))
