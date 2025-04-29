package dev.librecybernetics.data

import scala.compiletime.summonFrom
import scala.util.NotGiven

case class Money[ICurrencyCode <: CurrencyCode](
    amount: BigDecimal,
    currency: ICurrencyCode
)

object Money:
  def safeOp[ICurrencyCode <: CurrencyCode](
      op: BigDecimal => BigDecimal => BigDecimal
  )(a: Money[ICurrencyCode], b: Money[ICurrencyCode]): Money[ICurrencyCode] =
    Money(op(a.amount)(b.amount), a.currency)

  def unsafeOp[
      CurrencyCodeA <: CurrencyCode,
      CurrencyCodeB <: CurrencyCode
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
    * References:
    *  - https://github.com/scala/scala3/issues/23065#issuecomment-2838049601
    *  - https://rockthejvm.slack.com/archives/C010Q93136X/p1745908246504409?thread_ts=1745883490.640999&cid=C010Q93136X
    *  - https://discord.com/channels/1329817491249299526/1331325047230562366/1366712799119806574
    *
    * @param op
    *   A curried operation to be applied.
    */
  transparent inline def op[
      CurrencyCodeA <: CurrencyCode,
      CurrencyCodeB <: CurrencyCode
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

  extension [CurrencyCodeA <: CurrencyCode](a: Money[CurrencyCodeA])
    transparent inline def +[CurrencyCodeB <: CurrencyCode](
        b: Money[CurrencyCodeB]
    ) =
      op((a: BigDecimal) => a + _)(a, b)
  end extension
end Money
