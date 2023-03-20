package dev.librecybernetics.parser.toml.scalar

import cats.implicits.*
import cats.parse.{Parser, Parser0}

import dev.librecybernetics.parser.*

private def toDouble(
    sign: Option[Sign],
    string: String
): Double =
  sign
    .collect { case Sign.Minus => (bi: Double) => -bi }
    .getOrElse(identity[Double])
    .apply(string.toDouble)

private val specialDouble: Parser[Double] =
  (sign.?.with1 ~ Parser.fromStringMap(
    Map(
      "nan" -> Double.NaN,
      "inf" -> Double.PositiveInfinity
    )
  )).map {
    case (Some(Sign.Minus), d) => -d
    case (_, d)                => d
  }

private val numericWholeDouble: Parser[(Option[Sign], String)] =
  sign.?.with1 ~ Decimal.literal

private val numericDecimalDouble: Parser[String] =
  (Parser.char('.') *> Decimal.literal)

private val numericExponentDouble: Parser[String] =
  (Parser.charIn(Set('e', 'E')) *> sign.string.? ~ Decimal.literal)
    .map((s, e) => "e" + s.fold("")(identity) + e)

private val numericDouble: Parser[Double] =
  (numericWholeDouble ~ numericDecimalDouble.? ~ numericExponentDouble.?)
    .collect {
      case d @ (((_, _), Some(_)), _) => d
      case e @ (((_, _), _), Some(_)) => e
    }
    .map { case (((s, w), f), e) =>
      val fs = f.fold("")("." + _)
      val es = e.fold("")(identity)
      toDouble(s, s"$w$fs$es")
    }

private[toml] val float: Parser[Double] = numericDouble.backtrack | specialDouble
