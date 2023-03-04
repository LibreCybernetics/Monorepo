package dev.librecybernetics.types

import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

// TODO: Create unsigned-scalacheck for this Gen
val unsignedByteGen: Gen[UnsignedByte] = Gen.choose[Byte](-128.toByte, 127.toByte).map(_.toUnsignedByte)

class UnsignedByteSpec extends AnyWordSpec with ScalaCheckPropertyChecks {
  "All UnsignedByte" when {
    "examples" should {
      "match expected values" in {
        0.toByte.toUnsignedByte.show shouldBe "0"
        10.toByte.toUnsignedByte.show shouldBe "10"
        128.toByte.toUnsignedByte.show shouldBe "128"
        130.toByte.toUnsignedByte.show shouldBe "130"
        200.toByte.toUnsignedByte.show shouldBe "200"
        val r1 = 200.toShort.toUnsignedByte[Either[IllegalArgumentException, _]].toOption.get
        r1.show shouldBe "200"
        val r2 = 200.toInt.toUnsignedByte[Either[IllegalArgumentException, _]].toOption.get
        r2.show shouldBe "200"
        val r3 = 200.toLong.toUnsignedByte[Either[IllegalArgumentException, _]].toOption.get
        r3.show shouldBe "200"
      }

      "raise error when out of bounds" in {
        val ex1 = 1000.toShort.toUnsignedByte[Either[IllegalArgumentException, _]].left.toOption.get
        ex1 should have message "Given value 1000 doesn't satisfy 0 <= 1000 < 256"
        val ex2 = 1000.toInt.toUnsignedByte[Either[IllegalArgumentException, _]].left.toOption.get
        ex2 should have message "Given value 1000 doesn't satisfy 0 <= 1000 < 256"
        val ex3 = 1000.toLong.toUnsignedByte[Either[IllegalArgumentException, _]].left.toOption.get
        ex3 should have message "Given value 1000 doesn't satisfy 0 <= 1000 < 256"
      }
    }

    "wider types" should {
      "return unchanged" in {
        forAll(Gen.choose[Short](0, 255)) { s =>
          s
            .toUnsignedByte[Either[IllegalArgumentException, _]]
            .map { (ub: UnsignedByte) => ub.toShort } shouldBe Right(s)
        }

        forAll(Gen.choose[Int](0, 255)) { i =>
          i
            .toUnsignedByte[Either[IllegalArgumentException, _]]
            .map { (ub: UnsignedByte) => ub.toInt } shouldBe Right(i)
        }

        forAll(Gen.choose[Long](0, 255)) { l =>
          l
            .toUnsignedByte[Either[IllegalArgumentException, _]]
            .map { (ub: UnsignedByte) => ub.toLong } shouldBe Right(l)
        }
      }
    }

    "operations" when {
      "|" in {
        forAll(unsignedByteGen, unsignedByteGen) { (uba, ubb) =>
          (uba | ubb).toByte shouldBe (uba.toShort | ubb.toShort).toByte
        }
      }

      "&" in {
        forAll(unsignedByteGen, unsignedByteGen) { (uba, ubb) =>
          (uba & ubb).toByte shouldBe (uba.toShort & ubb.toShort).toByte
        }
      }

      "^" in {
        forAll(unsignedByteGen, unsignedByteGen) { (uba, ubb) =>
          (uba ^ ubb).toByte shouldBe (uba.toShort ^ ubb.toShort).toByte
        }
      }

      "+" in {
        forAll(unsignedByteGen, unsignedByteGen) { (uba, ubb) =>
          (uba + ubb).toByte shouldBe (uba.toShort + ubb.toShort).toByte
        }
      }

      "-" in {
        forAll(unsignedByteGen, unsignedByteGen) { (uba, ubb) =>
          (uba - ubb).toByte shouldBe (uba.toShort - ubb.toShort).toByte
        }
      }

      "*" in {
        forAll(unsignedByteGen, unsignedByteGen) { (uba, ubb) =>
          (uba * ubb).toByte shouldBe (uba.toShort * ubb.toShort).toByte
        }
      }

      "/" in {
        forAll(unsignedByteGen, unsignedByteGen) { (uba, ubb) =>
          whenever(ubb.toByte != 0) {
            (uba / ubb).toByte shouldBe (uba.toShort / ubb.toShort).toByte
          }
        }
      }

      "%" in {
        forAll(unsignedByteGen, unsignedByteGen) { (uba, ubb) =>
          (uba % ubb).toByte shouldBe (uba.toShort % ubb.toShort).toByte
        }
      }
    }
  }
}
