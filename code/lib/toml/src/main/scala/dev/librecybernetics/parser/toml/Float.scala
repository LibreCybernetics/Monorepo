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
  lazy val tomlFloat: Parser[Double] =
    ((plus | minus).?.with1 ~ Decimal.integerSep ~ (Parser.char('.') *> Decimal.integerSep).?).map {
      case ((s, w), Some(f)) => toDouble(s, s"$w.$f")
      case ((s, w), None)    => toDouble(s, s"$w")
    }
