package dev.librecybernetics.parser.toml

import cats.parse.Parser

import dev.librecybernetics.parser.*

private[toml] val emptyOrComment: Parser[Unit] =
  ((spaces ~ comment.?).with1 ~ newline).backtrack.void
