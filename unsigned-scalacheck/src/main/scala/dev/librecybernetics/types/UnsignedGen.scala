package dev.librecybernetics.types

import org.scalacheck.Gen

import UnsignedShort.toUnsignedShort

val unsignedByteGen: Gen[UnsignedByte]   = Gen.choose[Byte](Byte.MinValue, Byte.MaxValue).map(_.toUnsignedByte)
val unsignedShortGen: Gen[UnsignedShort] = Gen.choose[Short](Short.MinValue, Short.MaxValue).map(_.toUnsignedShort)
