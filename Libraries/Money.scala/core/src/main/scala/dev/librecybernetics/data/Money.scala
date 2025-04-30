package dev.librecybernetics.data

import scala.compiletime.summonFrom
import scala.util.NotGiven
import cats.ApplicativeError
import dev.librecybernetics.data.Money.Error.CurrencyMismatch

case class Money[ICurrencyCode <: CurrencyCode](
    amount: BigDecimal,
    currency: ICurrencyCode
)

object Money:
  enum Error:
    case CurrencyMismatch(a: CurrencyCode, b: CurrencyCode)
  end Error

  def safeOp[ICurrencyCode <: CurrencyCode](
      op: BigDecimal => BigDecimal => BigDecimal
  )(a: Money[ICurrencyCode], b: Money[ICurrencyCode]): Money[ICurrencyCode] =
    Money(op(a.amount)(b.amount), a.currency)

  def unsafeOp[
      CurrencyCodeA <: CurrencyCode,
      CurrencyCodeB <: CurrencyCode,
      F[_]: [FI[_]] =>> ApplicativeError[FI, Money.Error.CurrencyMismatch]
  ](
      op: BigDecimal => BigDecimal => BigDecimal
  )(
      a: Money[CurrencyCodeA],
      b: Money[CurrencyCodeB]
  ): F[Money[CurrencyCodeA]] =
    if (a.currency == b.currency)
      ApplicativeError().pure(Money(op(a.amount)(b.amount), a.currency))
    else
      ApplicativeError().raiseError(CurrencyMismatch(a.currency, b.currency))

  /** Performs an operation between two amounts of money. The operation is
    * handled safely if the currencies match, otherwise handled as unsafe.
    *
    * Thanks to Daniel Ciocîrlan (Rock the JVM), Dawid Łakomy, Oron Port
    * (DFiant, Scala SIP Committee) for guidance on summonFrom usage for this
    * use case.
    *
    * References:
    *   - https://github.com/scala/scala3/issues/23065#issuecomment-2838049601
    *   - https://rockthejvm.slack.com/archives/C010Q93136X/p1745908246504409?thread_ts=1745883490.640999&cid=C010Q93136X
    *   - https://discord.com/channels/1329817491249299526/1331325047230562366/1366712799119806574
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

      // NOTE: This only means that the currency codes are not necessarily equal at compile time
      case neq: NotGiven[CurrencyCodeA =:= CurrencyCodeB] =>
        unsafeOp(op)(a, b)
    }

  extension (scalar: BigDecimal)
    inline def *[CurrencyCodeA <: CurrencyCode](money: Money[CurrencyCodeA]): Money[CurrencyCodeA] =
      Money[CurrencyCodeA](scalar * money.amount, money.currency)
  end extension

  extension [CurrencyCodeA <: CurrencyCode](a: Money[CurrencyCodeA])
    transparent inline def +[CurrencyCodeB <: CurrencyCode](b: Money[CurrencyCodeB]) =
      op((a: BigDecimal) => a + _)(a, b)

    transparent inline def -[CurrencyCodeB <: CurrencyCode](b: Money[CurrencyCodeB]) =
      op((a: BigDecimal) => a - _)(a, b)

    inline def *(scalar: BigDecimal): Money[CurrencyCodeA] =
      Money(a.amount * scalar, a.currency)

    inline def /(quotient: BigDecimal): Money[CurrencyCodeA] =
      Money(a.amount / quotient, a.currency)
  end extension
end Money
