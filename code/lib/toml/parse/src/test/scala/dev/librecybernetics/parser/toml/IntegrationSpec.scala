package dev.librecybernetics.parser.toml

import java.time.{ZoneOffset, OffsetDateTime}
import scala.language.implicitConversions

import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.parser.*
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.given

class IntegrationSpec extends AnyWordSpec {
  "TOML" when {
    "Valid" should {
      Map(
        // Test case from toml-test: https://github.com/BurntSushi/toml-test
        """dates = [
          |  1987-07-05T17:45:00Z,
          |  1979-05-27T07:32:00Z,
          |  2006-06-01T11:00:00Z,
          |]
          |comments = [
          |                1,
          |                2, #this is ok
          |       ]
        """.stripMargin ->
          TOML.Map(
            Map(
              "dates"    -> TOML.Array(
                Seq(
                  OffsetDateTime.of(1987, 7, 5, 17, 45, 0, 0, ZoneOffset.UTC),
                  OffsetDateTime.of(1979, 5, 27, 7, 32, 0, 0, ZoneOffset.UTC),
                  OffsetDateTime.of(2006, 6, 1, 11, 0, 0, 0, ZoneOffset.UTC)
                )
              ),
              "comments" -> TOML.Array(Seq(1, 2))
            )
          ),
        // Test case from toml: https://github.com/toml-lang/toml
        """# This is a TOML document.
          |
          |title = "TOML Example"
          |
          |[owner]
          |name = "Tom Preston-Werner"
          |dob = 1979-05-27T07:32:00-08:00 # First class dates
          |
          |[database]
          |server = "192.168.1.1"
          |ports = [ 8000, 8001, 8002 ]
          |connection_max = 5000
          |enabled = true
          |
          |[servers]
          |
          |  # Indentation (tabs and/or spaces) is allowed but not required
          |  [servers.alpha]
          |  ip = "10.0.0.1"
          |  dc = "eqdc10"
          |
          |  [servers.beta]
          |  ip = "10.0.0.2"
          |  dc = "eqdc10"
          |
          |[clients]
          |data = [ ["gamma", "delta"], [1, 2] ]
          |
          |# Line breaks are OK when inside arrays
          |hosts = [
          |  "alpha",
          |  "omega"
          |]
        """.stripMargin ->
          TOML.Map(
            Map(
              "database" -> TOML.Map(
                Map(
                  "server"         -> "192.168.1.1",
                  "ports"          -> TOML.Array(Seq(8000, 8001, 8002)),
                  "connection_max" -> 5000,
                  "enabled"        -> true
                )
              ),
              "clients"  -> TOML.Map(
                Map(
                  "data"  -> TOML.Array(
                    Seq(
                      TOML.Array(Seq("gamma", "delta")),
                      TOML.Array(Seq(1, 2))
                    )
                  ),
                  "hosts" -> TOML.Array(
                    Seq(
                      "alpha",
                      "omega"
                    )
                  )
                )
              ),
              "servers"  -> TOML.Map(
                Map(
                  "alpha" -> TOML.Map(
                    Map(
                      "ip" -> "10.0.0.1",
                      "dc" -> "eqdc10"
                    )
                  ),
                  "beta"  -> TOML.Map(
                    Map(
                      "ip" -> "10.0.0.2",
                      "dc" -> "eqdc10"
                    )
                  )
                )
              ),
              "owner"    -> TOML.Map(
                Map(
                  "name" -> "Tom Preston-Werner",
                  "dob"  -> OffsetDateTime.of(1979, 5, 27, 7, 32, 0, 0, ZoneOffset.ofHoursMinutes(-8, 0))
                )
              ),
              "title"    -> "TOML Example"
            )
          ),
        """nest = [
          |        [
          |                ["a"],
          |                [1, 2, [3]]
          |        ]
          |       ]
        """.stripMargin ->
          TOML.Map(
            Map(
              "nest" -> TOML.Array(
                Seq(
                  TOML.Array(
                    Seq(
                      TOML.Array(Seq("a")),
                      TOML.Array(Seq(1, 2, TOML.Array(Seq(3))))
                    )
                  )
                )
              )
            )
          )
      ) foreach { (s, t) =>
        s in genericSuccess(Toml.toml)(s, t)
      }
    }
  }
}
