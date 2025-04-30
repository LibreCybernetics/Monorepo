package example

import dev.librecybernetics.data.{CurrencyCode, Money}

@main
def main(args: String*): Unit = {
  val input1 = Console.in.readLine()
  val input2 = Console.in.readLine()
  (input1, input2) match {
    case (currencyCode1: CurrencyCode, currencyCode2: CurrencyCode) =>
      val a = Money(10, currencyCode1)
      val b = Money(20, currencyCode2)
      val r_dynamic = a + b
      println(s"Result Dynamic: $r_dynamic")
      val r_same: Money[?] = Money(10, "MXN") + Money(20, "MXN")
      println(s"Known Result Same: $r_same")
      val r_diff = Money(10, "MXN") + Money(20, "USD")
      println(s"Known Result Diff: $r_diff")
  }
}
