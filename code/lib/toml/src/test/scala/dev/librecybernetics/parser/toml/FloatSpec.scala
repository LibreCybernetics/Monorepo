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
        "-2E-2"                 -> -2e-2d,
        "224_617.445_991_228"   -> 224_617.445_991_228d,
        "224_617.445_991_2e2_8" -> 224_617.445_991_2e2_8d
      ) foreach { (s, d) =>
        s in {
          val Right("", r) = Float.tomlFloat.parse(s): @unchecked
          r shouldBe d
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
          val Left(err) = Float.tomlFloat.parse(s): @unchecked
          err.input foreach (_ shouldBe s)
        }
      }
    }
  }
}
