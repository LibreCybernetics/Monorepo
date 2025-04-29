package example

import scala.util.NotGiven
import scala.compiletime.summonFrom

type ISOCurrencyCodes = "MXN" | "USD"
type CurrencyCodes = ISOCurrencyCodes

case class Money[CurrencyCode <: CurrencyCodes](
    amount: BigDecimal,
    currency: CurrencyCode
)

object Money:
  def safeOp[CurrencyCode <: CurrencyCodes](
      op: BigDecimal => BigDecimal => BigDecimal
  )(a: Money[CurrencyCode], b: Money[CurrencyCode]): Money[CurrencyCode] =
    Money(op(a.amount)(b.amount), a.currency)

  def unsafeOp[
      CurrencyCodeA <: CurrencyCodes,
      CurrencyCodeB <: CurrencyCodes
  ](
      op: BigDecimal => BigDecimal => BigDecimal
  )(
      a: Money[CurrencyCodeA],
      b: Money[CurrencyCodeB]
  ): Either[String, Money[CurrencyCodeA]] =
    if (a.currency == b.currency)
      Right(Money(op(a.amount)(b.amount), a.currency))
    else Left(s"Cannot do operation on mixed ${a.currency} and ${b.currency}")

  /** Performs an operation between two amounts of money. The operation is
    * handled safely if the currencies match, otherwise handled as unsafe.
    *
    * Thanks to Daniel Ciocîrlan (Rock the JVM), Dawid Łakomy, Oron Port
    * (DFiant, Scala SIP Committee) for guidance on summonFrom usage for this
    * use case.
    *
    * @param op
    *   A curried operation to be applied.
    */
  transparent inline def op[
      CurrencyCodeA <: CurrencyCodes,
      CurrencyCodeB <: CurrencyCodes
  ](op: BigDecimal => BigDecimal => BigDecimal)(
      a: Money[CurrencyCodeA],
      b: Money[CurrencyCodeB]
  ) =
    summonFrom {
      case eq: (CurrencyCodeA =:= CurrencyCodeB) =>
        // TODO: Use eq to change the type rather than .asInstanceOf
        safeOp(op)(a, b.asInstanceOf[Money[CurrencyCodeA]])
      case neq: NotGiven[CurrencyCodeA =:= CurrencyCodeB] =>
        unsafeOp(op)(a, b)
    }

  extension [CurrencyCodeA <: CurrencyCodes](a: Money[CurrencyCodeA])
    transparent inline def +[CurrencyCodeB <: CurrencyCodes](
        b: Money[CurrencyCodeB]
    ) =
      op((a: BigDecimal) => a + _)(a, b)
  end extension
end Money

@main
def main(args: String*): Unit = {
  val input1 = Console.in.readLine()
  val input2 = Console.in.readLine()
  (input1, input2) match {
    case (currencyCode1: CurrencyCodes, currencyCode2: CurrencyCodes) =>
      val a = Money(10, currencyCode1)
      val b = Money(20, currencyCode2)
      val r_dynamic = a + b
      println(s"Result Dynamic: $r_dynamic")
      val r_same: Money[?] = Money(10, "MXN") + Money(20, "MXN")
      println(s"Known Result Same: $r_same")
      val r_diff: Either[String, Money[?]] =
        Money(10, "MXN") + Money(20, "USD")
      println(s"Known Result Diff: $r_diff")
  }
}
