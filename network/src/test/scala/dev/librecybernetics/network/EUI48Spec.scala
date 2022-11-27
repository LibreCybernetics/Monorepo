package dev.librecybernetics.network

import scala.collection.immutable.ArraySeq

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class EUI48Spec extends AnyWordSpec with ScalaCheckPropertyChecks {
  "EUI48" should {
    "examples" when {
      "throw illegal arguments" in {
        an[IllegalArgumentException] should be thrownBy
          EUI48(ArraySeq.empty[Octet])
        an[IllegalArgumentException] should be thrownBy
          EUI48(
            ArraySeq.from(
              Seq[Short](0x0, 0x0, 0x0, 0x0, 0x0).map(Octet(_))
            )
          )
        an[IllegalArgumentException] should be thrownBy
          EUI48(
            ArraySeq.from(
              Seq[Short](0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0).map(Octet(_))
            )
          )
      }
    }
    "work" when {
      "fuzzing" in {
        forAll(eui48Gen) { (eui48: EUI48) =>
          println(eui48)
          eui48.administred shouldBe a[EUI48.Administred]
          eui48.cast shouldBe a[EUI48.Cast]
        }
      }
    }
  }
}
