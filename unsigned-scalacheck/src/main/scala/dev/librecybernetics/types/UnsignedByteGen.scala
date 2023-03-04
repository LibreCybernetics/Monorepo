package dev.librecybernetics.types

import org.scalacheck.Gen

val unsignedByteGen: Gen[UnsignedByte] = Gen.choose[Byte](-128.toByte, 127.toByte).map(_.toUnsignedByte)
