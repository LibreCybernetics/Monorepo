package dev.librecybernetics

import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class OctetSpec extends AnyWordSpec with ScalaCheckPropertyChecks {
  "All Octets" when {
    "examples" should {
      "match expected values" in {
        Octet(0x00).toHexString shouldBe "00"
        Octet(0xaa).toHexString shouldBe "AA"
        Octet(0xff).toHexString shouldBe "FF"
      }
    }

    "written and read from hex" should {
      "equal initial value" in {
        forAll(Gen.choose[Short](-128, 384)) { n =>
          val cond = n >= 0 && n < 256

          if (!cond)
            an[IllegalArgumentException] should be thrownBy Octet(n.toShort)

          whenever(cond) {
            Octet.fromHexString(Octet(n).toHexString) shouldBe Octet(n)
          }
        }
      }
    }
  }
}
