package dev.librecybernetics.parser.toml.scalar

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.genericSuccess

class IntegerSpec extends AnyWordSpec {
  "Integer" when {
    "Valid Decimal Integer" should {
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
        s in genericSuccess(integer)(s, i)
      }
    }

    "Valid Hexadecimal Integer" should {
      Map(
        "0xDEADBEEF"  -> 0xdeadbeefL,
        "0xdeadbeef"  -> 0xdeadbeefL,
        "0xdead_beef" -> 0xdead_beefL
      ) foreach { (s, i) =>
        s in genericSuccess(integer)(s, i)
      }
    }

    "Valid Octal Integer" should {
      Map(
        "0o01234567" -> 342391,
        "0o755"      -> 493
      ) foreach { (s, i) =>
        s in genericSuccess(integer)(s, i)
      }
    }

    "Valid Binary Integer" should {
      Map(
        "0b11010110" -> 0xd6
      ) foreach { (s, b) =>
        s in genericSuccess(integer)(s, b)
      }
    }
  }
}
