package dev.librecybernetics.parser.toml

import cats.implicits.*
import cats.data.NonEmptyList
import cats.parse.Parser
import dev.librecybernetics.types.TOML

// TODO: tailrec
def transformDottedToNestedMap(
    key: NonEmptyList[String],
    value: TOML
): TOML.Map =
  val NonEmptyList(headKey, tailKeys) = key
  tailKeys match
    case h :: t =>
      val nel = NonEmptyList(h, t)
      TOML.Map(
        Map(headKey -> transformDottedToNestedMap(nel, value))
      )
    case _      =>
      TOML.Map(
        Map(headKey -> value)
      )

val keyMap: Parser[TOML.Map] =
  (
    dottedkey,
    assignment *> allValues
  ).mapN { transformDottedToNestedMap }
