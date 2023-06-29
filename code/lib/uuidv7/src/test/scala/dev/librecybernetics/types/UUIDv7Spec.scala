package dev.librecybernetics.types

import java.time.{Instant, ZonedDateTime, ZoneId}

import cats.data.Validated
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class UUIDv7Spec extends AnyWordSpec:
  "official test vector" should {
    // https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html#name-example-of-a-uuidv7-value

    val uuid: Validated[UUIDv7.Error, UUIDv7] =
      UUIDv7(
        Instant.ofEpochMilli(0x17f22e279b0L),
        // 0xCC3
        // 0x18_C4_DC_0C_0C_07_39_8F << 2 = 0x63_13_70_30_30_1c_e8_00
        Array(0xcc, 0x36, 0x31, 0x37, 0x03, 0x03, 0x01, 0xc3, 0xe8, 0x00).map(_.toByte)
      )

    "value should succeed" in {
      uuid shouldBe a[Validated.Valid[UUIDv7]]
    }

    "time should match" in {
      uuid map { uuid =>
        val odt = ZonedDateTime.ofInstant(uuid.timestamp, ZoneId.of("GMT-5"))
        odt shouldBe ZonedDateTime.of(
          2022,
          2,
          22,
          14,
          22,
          22,
          0,
          ZoneId.of("GMT-5")
        )
      }
    }
  }
