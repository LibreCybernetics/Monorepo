package dev.librecybernetics.parser.toml

import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.types.TOML
import dev.librecybernetics.parser.*

class ArrayOfTablesSpec extends AnyWordSpec {
  "ArrayOfTables" when {
    "Valid Input" should {
      val input1 =
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
        input1 -> TOML.Map(Map(
          "products" -> TOML.Array(Seq(
            TOML.KeyValue("name", TOML.String("Hammer")),
            TOML.KeyValue("sku", TOML.Integer(BigInt(738594937)))
          ))
        )),
        input2 -> TOML.Map(Map(
          "products" -> TOML.Array(Nil)
        )),
        input3 -> TOML.Map(Map(
          "products" -> TOML.Array(Seq(
            TOML.KeyValue("name", TOML.String("Nail")),
            TOML.KeyValue("sku", TOML.Integer(BigInt(284758393))),
            TOML.KeyValue("color", TOML.String("gray"))
          ))
        ))
      ) foreach { (s, aot) =>
        s in genericTest(ArrayOfTables.arrayOfTables)(s, aot)
      }
    }
  }
}
