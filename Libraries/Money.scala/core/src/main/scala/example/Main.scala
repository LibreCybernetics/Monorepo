package example

import scala.util.NotGiven

type ISOCurrencyCodes = "MXN" | "USD"
type CurrencyCodes = ISOCurrencyCodes

type EqOrNeq[A, B] = A =:= B | NotGiven[A =:= B]
type Result[CurrencyCodeA, CurrencyCodeB, E <: EqOrNeq[CurrencyCodeA, CurrencyCodeB]] = E match
  case CurrencyCodeA =:= CurrencyCodeB => Money[CurrencyCodeA]
  case NotGiven[CurrencyCodeA =:= CurrencyCodeB] => Either[String, Money[CurrencyCodeA]]
end Result

case class Money[ICurrencyCode <: CurrencyCodes](
    amount: BigDecimal,
    currency: ICurrencyCode
) {
  type CurrencyCode = ICurrencyCode

  def +[OCurrencyCode <: CurrencyCodes](
      other: Money[OCurrencyCode]
  )(using ev: EqOrNeq[CurrencyCode, OCurrencyCode]): Result[CurrencyCode, OCurrencyCode, ev.type] =
    ev match
      case eq: (CurrencyCode =:= OCurrencyCode) =>
        Money(amount + other.amount, currency)
      case neq: NotGiven[CurrencyCode =:= OCurrencyCode] =>
        if (currency == other.currency) Right(Money(amount + other.amount, currency))
        else Left("Currencies don't match")
    end match
}

@main
def main(args: String*): Unit = {
  val input1 = Console.in.readLine()
  val input2 = Console.in.readLine()
  (input1, input2) match {
    case (currencyCode1: CurrencyCodes, currencyCode2: CurrencyCodes) =>
      val a = Money(10, currencyCode1)
      val b = Money(20, currencyCode2)
      val r: Money["MXN"] = Money(10, "MXN") + Money(20, "MXN")
      val r2: Either[String, Money["MXN"]] = Money(10, "MXN") + Money(20, "USD")
  }
}
