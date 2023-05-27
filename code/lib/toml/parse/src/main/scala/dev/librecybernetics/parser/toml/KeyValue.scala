package dev.librecybernetics.parser.toml

import cats.data.NonEmptyList
import cats.implicits.*
import cats.parse.Parser

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML

/** ```
  * keyval-sep = ws %x3D ws ; =
  * ```
  */
private val assignment: Parser[Unit] =
  equal.surroundedBy(whitespaces)


/**
 * ```
 * keyval = key keyval-sep val
 * ```
 */
private[toml] val keyValue: Parser[TOML.Map] =
  (
    whitespaces.with1 *> key,
    assignment *> allValues <* spaces <* comment.?
  )
    .mapN { transformDottedKey }
    .backtrack
    .withContext("key-value")
