package dev.librecybernetics.types

import org.scalacheck.Gen

import UnsignedByte.toUnsignedByte
import UnsignedShort.toUnsignedShort
import UnsignedInt.toUnsignedInt

val unsignedByteGen: Gen[UnsignedByte]   = Gen.choose[Byte](Byte.MinValue, Byte.MaxValue).map(_.toUnsignedByte)
val unsignedShortGen: Gen[UnsignedShort] = Gen.choose[Short](Short.MinValue, Short.MaxValue).map(_.toUnsignedShort)
val unsignedIntGen: Gen[UnsignedInt]     = Gen.choose[Int](Int.MinValue, Int.MaxValue).map(_.toUnsignedInt)
