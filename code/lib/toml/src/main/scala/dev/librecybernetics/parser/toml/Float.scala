package dev.librecybernetics.parser.toml

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
  lazy val nan: Parser[Double] = Parser.string("nan").map(_ => Double.NaN)
  lazy val inf: Parser[Double] = Parser.string("inf").map(_ => Double.PositiveInfinity)
  lazy val specialDouble: Parser[Double] = (sign.?.with1 ~ (nan | inf)).map {
    case (Some(Sign.Minus), d) => -d
    case (_, d) => d
  }

  lazy val numericDouble: Parser0[Double] =
    (sign.?,
      Decimal.integerSep,
      (Parser.char('.') *> Decimal.integerSep).?,
      (Parser.charIn(Set('e', 'E')) *> sign.string.? ~ Decimal.integerSep).map(
        (s, e) => "e" + s.fold("")(identity) + e
      ).?
    ).mapN {
      case (s, w, f, e) =>
        val fs = f.fold("")("." + _)
        val es = e.fold("")(identity)
        toDouble(s, s"$w$fs$es")
    }.backtrack

  val float: Parser0[Double] = numericDouble | specialDouble