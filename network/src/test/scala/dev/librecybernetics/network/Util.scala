package dev.librecybernetics.network

import scala.collection.ArrayOps

import org.scalacheck.Gen

import dev.librecybernetics.types.unsignedByteGen

val octetGen: Gen[Octet] = unsignedByteGen.map(Octet.apply)
val eui48Gen: Gen[EUI48] = Gen.listOfN(6, octetGen).map(EUI48(_))
val eui64Gen: Gen[EUI64] = Gen.listOfN(8, octetGen).map(EUI64(_))
