package dev.librecybernetics

import scala.collection.ArrayOps

import org.scalacheck.Gen

val octetGen: Gen[Octet] = Gen.choose[Short](0,255).map(Octet(_))
val eui48Gen: Gen[EUI48] = Gen.listOfN(6, octetGen).map(EUI48(_))
