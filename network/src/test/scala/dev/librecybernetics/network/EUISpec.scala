package dev.librecybernetics.network

import scala.Function.const
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
              Seq.fill[Short](5)(0x0).map(Octet(_))
            )
          )
        an[IllegalArgumentException] should be thrownBy
          EUI48(
            ArraySeq.from(
              Seq.fill[Short](7)(0x0).map(Octet(_))
            )
          )
        an[IllegalArgumentException] should be thrownBy
          EUI64(ArraySeq.empty[Octet])
        an[IllegalArgumentException] should be thrownBy
          EUI64(
            ArraySeq.from(
              Seq.fill[Short](7)(0x0).map(Octet(_))
            )
          )
        an[IllegalArgumentException] should be thrownBy
          EUI64(
            ArraySeq.from(
              Seq.fill[Short](9)(0x0).map(Octet(_))
            )
          )
      }
    }
    "work" when {
      "fuzzing" in {
        forAll(eui48Gen) { (eui48: EUI48) =>
          eui48.administred shouldBe a[EUI.Administred]
          eui48.cast shouldBe a[EUI.Cast]
        }
        forAll(eui64Gen) { (eui64: EUI64) =>
          eui64.administred shouldBe a[EUI.Administred]
          eui64.cast shouldBe a[EUI.Cast]
        }
      }
    }
  }
}
