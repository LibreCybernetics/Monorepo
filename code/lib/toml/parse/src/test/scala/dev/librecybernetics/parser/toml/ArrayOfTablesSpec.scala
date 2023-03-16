package dev.librecybernetics.parser.toml

import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.types.TOML
import dev.librecybernetics.parser.*

class ArrayOfTablesSpec extends AnyWordSpec {
  "ArrayOfTables" when {
    "Valid Input" should {
      val input1         =
        """[[products]]
          |name = "Hammer"
          |sku = 738594937""".stripMargin
      val input2: String =
        """[[products]]""".stripMargin
      val input3: String =
        """[[products]]
          |name = "Nail"
          |sku = 284758393
          |
          |color = "gray"""".stripMargin

      Map(
        input1 -> TOML.Map(
          Map(
            "products" -> TOML.Array(
              Seq(
                TOML.Map(
                  Map(
                    "name" -> TOML.String("Hammer"),
                    "sku"  -> TOML.Integer(BigInt(738594937))
                  )
                )
              )
            )
          )
        ),
        input2 -> TOML.Map(
          Map(
            "products" -> TOML.Array(
              Seq(
                TOML.Map(Map.empty)
              )
            )
          )
        ),
        input3 -> TOML.Map(
          Map(
            "products" -> TOML.Array(
              Seq(
                TOML.Map(
                  Map(
                    "name"  -> TOML.String("Nail"),
                    "sku"   -> TOML.Integer(BigInt(284758393)),
                    "color" -> TOML.String("gray")
                  )
                )
              )
            )
          )
        )
      ) foreach { (s, aot) =>
        s in genericTest(ArrayOfTables.arrayOfTables)(s, aot)
      }
    }
  }
}
