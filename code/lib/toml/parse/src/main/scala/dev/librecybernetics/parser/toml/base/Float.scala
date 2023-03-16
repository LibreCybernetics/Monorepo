package dev.librecybernetics.parser.toml.base

import cats.parse.{Parser, Parser0}
import cats.implicits.*

private def toDouble(
    sign: Option[Sign],
    string: String
): Double =
  val transformSign = sign
    .collect { case Sign.Minus => (bi: Double) => -bi }
    .getOrElse(identity[Double])
  transformSign(string.toDouble)

object Float:
  val nan: Parser[Double] = Parser.string("nan").map(_ => Double.NaN)
  val inf: Parser[Double] = Parser.string("inf").map(_ => Double.PositiveInfinity)

  val specialDouble: Parser[Double] = (sign.?.with1 ~ (nan | inf)).map {
    case (Some(Sign.Minus), d) => -d
    case (_, d)                => d
  }

  val numericDouble: Parser[Double] =
    (
      sign.?.with1 ~ Decimal.literal ~
        (Parser.char('.') *> Decimal.literal).? ~
        (Parser.charIn(Set('e', 'E')) *> sign.string.? ~ Decimal.literal)
          .map((s, e) => "e" + s.fold("")(identity) + e)
          .?
    ).map { case (((s, w), f), e) =>
      val fs = f.fold("")("." + _)
      val es = e.fold("")(identity)
      toDouble(s, s"$w$fs$es")
    }

  val float: Parser[Double] = numericDouble.backtrack | specialDouble
