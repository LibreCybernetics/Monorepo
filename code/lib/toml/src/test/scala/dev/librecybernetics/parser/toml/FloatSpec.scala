package dev.librecybernetics.parser.toml

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class FloatSpec extends AnyWordSpec {
  "Float" when {
    "Valid Simple Float" should {
      Map(
        "+1.0"                  -> 1.0d,
        "3.1415"                -> 3.1415d,
        "-0.01"                 -> -0.01d,
        "5e+22"                 -> 5e+22d,
        "1e06"                  -> 1e06d,
        "6.626e-34"             -> 6.626e-34d,
        "-2E-2"                 -> -2e-2d,
        "224_617.445_991_228"   -> 224_617.445_991_228d,
        "224_617.445_991_2e2_8" -> 224_617.445_991_2e2_8d,
        "inf" -> Double.PositiveInfinity,
        "+inf" -> Double.PositiveInfinity,
        "-inf" -> Double.NegativeInfinity,
      ) foreach { (s, d) =>
        s in {
          val Right("", r) = Float.float.parse(s): @unchecked
          r shouldBe d
        }
      }
    }

    "Valid NaN" should {
      Map(
        "nan" -> Double.NaN,
        "+nan" -> Double.NaN,
        "-nan" -> Double.NaN,
      ) foreach { (s, d) =>
        s in {
          val Right("", r) = Float.float.parse(s): @unchecked
          assert(r.isNaN)
        }
      }
    }

    "Invalid Simple Float" should {
      Set(
        ".7",
        "7.",
        "7e",
        "7.e",
        "3.e+20"
      ) foreach { s =>
        s in {
          val Left(err) = Float.float.parse(s): @unchecked
          err.input foreach (_ shouldBe s)
        }
      }
    }
  }
}
