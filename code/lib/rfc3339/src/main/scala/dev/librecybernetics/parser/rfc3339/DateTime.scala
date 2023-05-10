package dev.librecybernetics.parser.rfc3339

import java.time.LocalDateTime

import cats.parse.Parser

import dev.librecybernetics.parser.*

/** date-time = full-date ("T" | " ") full-time
  */
private[parser] val dateTime: Parser[LocalDateTime] =
  ((date <* Parser.charIn('T', ' ')) ~ time)
    .map((date, time) => LocalDateTime.of(date, time))
