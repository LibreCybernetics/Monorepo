package dev.librecybernetics.types

import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class UnsignedByteSpec extends AnyWordSpec with ScalaCheckPropertyChecks {
  "All UnsignedByte" when {
    "examples" should {
      "match expected values" in {
        UnsignedByte(0).show shouldBe "0"
        UnsignedByte(10).show shouldBe "10"
        UnsignedByte(128.toByte).show shouldBe "128"
        UnsignedByte(130.toByte).show shouldBe "130"
      }
    }
  }
}
