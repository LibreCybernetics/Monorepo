package dev.librecybernetics.network

import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import dev.librecybernetics.types.toUnsignedByte

class OctetSpec extends AnyWordSpec with ScalaCheckPropertyChecks {
  "All Octets" when {
    "examples" should {
      "match expected values" in {
        Octet(0x00.toByte.toUnsignedByte).toHexString shouldBe "00"
        Octet(0xaa.toByte.toUnsignedByte).toHexString shouldBe "AA"
        Octet(0xff.toByte.toUnsignedByte).toHexString shouldBe "FF"
      }
    }

    "written and read from hex" should {
      "equal initial value" in {
        forAll(unsignedByteGen) { ub =>
          Octet.fromHexString(Octet(ub).toHexString) shouldBe Octet(ub)
        }
      }
    }
  }
}
