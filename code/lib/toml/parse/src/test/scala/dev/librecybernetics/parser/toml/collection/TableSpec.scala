package dev.librecybernetics.parser.toml.collection

import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.genericSuccess
import dev.librecybernetics.types.TOML

class TableSpec extends AnyWordSpec {
  "Table" when {
    "Valid Cases" should {
      Map(
        "[table]" -> TOML.Map(
          Map("table" -> TOML.Map(Map.empty))
        )
      ) foreach { (s, t) =>
        s in genericSuccess(table)(s, t)
      }
    }
  }
}
