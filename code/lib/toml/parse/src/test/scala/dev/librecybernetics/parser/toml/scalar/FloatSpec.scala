package dev.librecybernetics.parser.toml.scalar

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*
import dev.librecybernetics.parser.toml.scalar

class FloatSpec extends AnyWordSpec {
  "Float" when {
    "Valid Simple Float" should {
      (Map( // simple
        "2.0"  -> 2.0d,
        "+0.0" -> 0.0d,
        "+1.0" -> 1.0d,
        "-1.5" -> -1.5d,
        "4.2"  -> 4.2d
      ) ++ Map( // more complex
        "3.1415"                -> 3.1415d,
        "-0.01"                 -> -0.01d,
        "224_617.445_991_228"   -> 224_617.445_991_228d,
        "224_617.445_991_2e2_8" -> 224_617.445_991_2e2_8d,
        "1_000.000_1"           -> 1_000.000_1d,
        "1.000000000000001"     -> 1.000000000000001d,
        "0.000000000000001"     -> 0.000000000000001d
      ) ++ Map( // e and inf
        "5e+22"                -> 5e+22d,
        "1e06"                 -> 1e06d,
        "6.626e-34"            -> 6.626e-34d,
        "-2E-2"                -> -2e-2d,
        "1e+03"                -> 1e+03d,
        "-1.000000e-06"        -> -1.000000e-06d,
        "1_000.000e+06"        -> 1_000.000e+06d,
        "-123_456.789_012e-25" -> -123_456.789_012e-25d,
        "6.626e-34"            -> 6.626e-34,
        "inf"                  -> Double.PositiveInfinity,
        "+inf"                 -> Double.PositiveInfinity,
        "-inf"                 -> Double.NegativeInfinity
      )) foreach { (s, d) =>
        s in genericSuccess(scalar.float)(s, d)
      }
    }

    "Valid NaN" should {
      Map(
        "nan"  -> Double.NaN,
        "+nan" -> Double.NaN,
        "-nan" -> Double.NaN
      ) foreach { (s, d) =>
        s in {
          val Right("", r) = scalar.float.parse(s): @unchecked
          assert(r.isNaN)
        }
      }
    }

    "Invalid Simple Float" should {
      val error: Seq[String] = Seq(
        "must match one of the strings: {\"inf\", \"nan\"}",
        "must be char: '+'",
        "must be char: '-'",
        "must be a char within the range of: ['0', '9']"
      )
      Map(
        ".7" -> error,
        "7." -> error,
        "7e" -> (error.dropRight(1) ++ error.drop(1)),
        "7.e" -> error,
        "3.e+20" -> error
      ) foreach { (s, message) =>
        s in genericFailure(scalar.float)(s, message*)
      }
    }
  }
}
