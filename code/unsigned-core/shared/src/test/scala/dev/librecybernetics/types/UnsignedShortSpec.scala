package dev.librecybernetics.types

import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import UnsignedShort.*

class UnsignedShortSpec extends AnyWordSpec with ScalaCheckPropertyChecks {
  "All UnsignedShort" when {
    "examples" should {
      "match expected values" in {
        0.toShort.toUnsignedShort.show shouldBe "0"
        32000.toShort.toUnsignedShort.show shouldBe "32000"
        34000.toShort.toUnsignedShort.show shouldBe "34000"
        50000.toShort.toUnsignedShort.show shouldBe "50000"
        65500.toShort.toUnsignedShort.show shouldBe "65500"

        val r2 = 60000.toInt.toUnsignedShort[Either[IllegalArgumentException, _]].toOption.get
        r2.show shouldBe "60000"
        val r3 = 60000.toLong.toUnsignedShort[Either[IllegalArgumentException, _]].toOption.get
        r3.show shouldBe "60000"
      }

      "raise error when out of bounds" in {
        val msg = "Given value 100000 doesn't satisfy 0 <= 100000 < 65536"
        val ex2 = 100000.toInt.toUnsignedShort[Either[IllegalArgumentException, _]].left.toOption.get
        ex2 should have message msg
        val ex3 = 100000.toLong.toUnsignedShort[Either[IllegalArgumentException, _]].left.toOption.get
        ex3 should have message msg
      }
    }

    "wider types" should {
      "return unchanged" in {
        forAll(Gen.choose[Int](0, 65535)) { i =>
          i
            .toUnsignedShort[Either[IllegalArgumentException, _]]
            .map { (ub: UnsignedShort) => ub.toInt } shouldBe Right(i)
        }

        forAll(Gen.choose[Long](0, 65535)) { l =>
          l
            .toUnsignedShort[Either[IllegalArgumentException, _]]
            .map { (ub: UnsignedShort) => ub.toLong } shouldBe Right(l)
        }
      }
    }

    "operations" when {
      "|" in {
        forAll(unsignedShortGen, unsignedShortGen) { (uba, ubb) =>
          (uba | ubb).toShort shouldBe (uba.toInt | ubb.toInt).toShort
        }
      }

      "&" in {
        forAll(unsignedShortGen, unsignedShortGen) { (uba, ubb) =>
          (uba & ubb).toShort shouldBe (uba.toInt & ubb.toInt).toShort
        }
      }

      "^" in {
        forAll(unsignedShortGen, unsignedShortGen) { (uba, ubb) =>
          (uba ^ ubb).toShort shouldBe (uba.toInt ^ ubb.toInt).toShort
        }
      }

      "+" in {
        forAll(unsignedShortGen, unsignedShortGen) { (uba, ubb) =>
          (uba + ubb).toShort shouldBe (uba.toInt + ubb.toInt).toShort
        }
      }

      "-" in {
        forAll(unsignedShortGen, unsignedShortGen) { (uba, ubb) =>
          (uba - ubb).toShort shouldBe (uba.toInt - ubb.toInt).toShort
        }
      }

      "*" in {
        forAll(unsignedShortGen, unsignedShortGen) { (uba, ubb) =>
          (uba * ubb).toShort shouldBe (uba.toInt * ubb.toInt).toShort
        }
      }

      "/" in {
        forAll(unsignedShortGen, unsignedShortGen) { (uba, ubb) =>
          whenever(ubb.toShort != 0) {
            (uba / ubb).toShort shouldBe (uba.toInt / ubb.toInt).toShort
          }
        }
      }

      "%" in {
        forAll(unsignedShortGen, unsignedShortGen) { (uba, ubb) =>
          (uba % ubb).toShort shouldBe (uba.toInt % ubb.toInt).toShort
        }
      }
    }
  }
}
