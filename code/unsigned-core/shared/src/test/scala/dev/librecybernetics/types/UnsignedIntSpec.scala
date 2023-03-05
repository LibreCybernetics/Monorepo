package dev.librecybernetics.types

import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import UnsignedInt.*

class UnsignedIntSpec extends AnyWordSpec with ScalaCheckPropertyChecks {
  "All UnsignedInt" when {
    "examples" should {
      "match expected values" in {
        0.toUnsignedInt.show shouldBe "0"
        4000000000L.toInt.toUnsignedInt.show shouldBe "4000000000"
        4200000000L.toInt.toUnsignedInt.show shouldBe "4200000000"

        val r3 = 60000.toLong.toUnsignedInt[Either[IllegalArgumentException, _]].toOption.get
        r3.show shouldBe "60000"
      }

      "raise error when out of bounds" in {
        val msg = "Given value 5000000000 doesn't satisfy 0 <= 5000000000 < 4294967296"
        val ex3 = 5_000_000_000L.toUnsignedInt[Either[IllegalArgumentException, _]].left.toOption.get
        ex3 should have message msg
      }
    }

    "wider types" should {
      "return unchanged" in {
        forAll(Gen.choose[Long](0L, 4294967295L)) { l =>
          whenever(0L <= l) {
            l
              .toUnsignedInt[Either[IllegalArgumentException, _]]
              .map { (ui: UnsignedInt) => ui.toLong } shouldBe Right(l)

          }
        }
      }
    }

    "operations" when {
      "|" in {
        forAll(unsignedIntGen, unsignedIntGen) { (uia, uib) =>
          (uia | uib).toInt shouldBe (uia.toLong | uib.toLong).toInt
        }
      }

      "&" in {
        forAll(unsignedIntGen, unsignedIntGen) { (uia, uib) =>
          (uia & uib).toInt shouldBe (uia.toLong & uib.toLong).toInt
        }
      }

      "^" in {
        forAll(unsignedIntGen, unsignedIntGen) { (uia, uib) =>
          (uia ^ uib).toInt shouldBe (uia.toLong ^ uib.toLong).toInt
        }
      }

      "+" in {
        forAll(unsignedIntGen, unsignedIntGen) { (uia, uib) =>
          (uia + uib).toInt shouldBe (uia.toLong + uib.toLong).toInt
        }
      }

      "-" in {
        forAll(unsignedIntGen, unsignedIntGen) { (uia, uib) =>
          (uia - uib).toInt shouldBe (uia.toLong - uib.toLong).toInt
        }
      }

      "*" in {
        forAll(unsignedIntGen, unsignedIntGen) { (uia, uib) =>
          (uia * uib).toInt shouldBe (uia.toLong * uib.toLong).toInt
        }
      }

      "/" in {
        forAll(unsignedIntGen, unsignedIntGen) { (uia, uib) =>
          whenever(uib.toInt != 0) {
            (uia / uib).toInt shouldBe (uia.toLong / uib.toLong).toInt
          }
        }
      }

      "%" in {
        forAll(unsignedIntGen, unsignedIntGen) { (uia, uib) =>
          (uia % uib).toInt shouldBe (uia.toLong % uib.toLong).toInt
        }
      }
    }
  }
}
