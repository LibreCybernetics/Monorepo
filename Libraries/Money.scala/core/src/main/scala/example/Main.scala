package example

import scala.compiletime.testing.typeCheckErrors

type ISOCurrencyCodes = "MXN" | "USD"
type CurrencyCodes = ISOCurrencyCodes

case class Money[CurrencyCode <: CurrencyCodes](
    amount: BigDecimal,
    currency: CurrencyCode
) {
  def +[OCurrencyCode <: CurrencyCodes](
      other: Money[OCurrencyCode]
  )(using ev: CurrencyCode =:= OCurrencyCode) =
    Money(amount + other.amount, currency)
}

@main
def main(args: String*): Unit = {
  val input = Console.in.readLine()
  input match {
    case currencyCode: CurrencyCodes =>
      val a = Money(10, currencyCode)
      val b = Money(20, currencyCode)
      val result = a + b
      println(s"Result: $result")
    case _ => println("Invalid input")
  }
}
