package dev.librecybernetics.parser

private[parser] val basicLatinLetters: Set[Char] =
  ('a' to 'z').toSet ++ ('A' to 'Z').toSet

private[parser] val latinDecimalDigits: Set[Char] =
  ('0' to '9').toSet
  
private[parser] val hexDigit: Set[Char] =
  latinDecimalDigits ++
    ('a' to 'f').toSet ++
    ('A' to 'F').toSet
