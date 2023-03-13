package dev.librecybernetics.parser

val basicLatinLetters: Set[Char] =
  ('a' to 'z').toSet ++ ('A' to 'Z').toSet

val latinDecimalDigits: Set[Char] =
  ('0' to '9').toSet
  
val hexDigit: Set[Char] =
  latinDecimalDigits ++
    ('a' to 'f').toSet ++
    ('A' to 'F').toSet
