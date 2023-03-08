package dev.librecybernetics.parser.toml

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class IntegerSpec extends AnyWordSpec {
  "Integer" when {
    "Valid integer" should {
      (Map(
        "+99" -> 99,
        "42"  -> 42,
        "0"   -> 0,
        "-17" -> -17
      ) ++ Map(
        "1_000"     -> 1000,
        "5_349_221" -> 5349221,
        "53_49_221" -> 5349221,
        "1_2_3_4_5" -> 12345
      )) foreach { (s, i) =>
        s in {
          val Right("", r) = integer.parse(s): @unchecked
          r.toInt shouldBe i
        }
      }
    }
  }
}
