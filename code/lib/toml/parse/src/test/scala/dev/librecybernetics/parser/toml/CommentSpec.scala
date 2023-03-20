package dev.librecybernetics.parser.toml

import cats.parse.Parser
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.types.TOML

class CommentSpec extends AnyWordSpec {
  "Comment" when {
    "Valid comment" should {
      "Parse Simple" in {
        val Right(("", r)) = comment.parse("# test"): @unchecked
        r shouldEqual TOML.Comment("test")
      }

      "Parse Multiline" in {
        val Right(("", r)) = comment.parse("""# test
            |# multi-line""".stripMargin): @unchecked
        r shouldEqual TOML.Comment("test\nmulti-line")
      }
    }

    "Invalid comment" should {
      "not parse" when {
        Map(
          "null"                      -> '\u0000',
          "Start of Heading"          -> '\u0001',
          "Start of Text"             -> '\u0002',
          "End of Text"               -> '\u0003',
          "End of Transmission"       -> '\u0004',
          "Enquiry"                   -> '\u0005',
          "Acknowledge"               -> '\u0006',
          "Bell"                      -> '\u0007',
          "Backspace"                 -> '\u0008',
          // NOTE: "Line Feed" -> 000A is \n
          "Line Tabulation"           -> '\u000B',
          "Form Feed"                 -> '\u000C',
          "Carriage Return"           -> '\u000D',
          "Shift Out"                 -> '\u000E',
          "Shift In"                  -> '\u000F',
          "Data Link Escape"          -> '\u0010',
          "Device Control 1"          -> '\u0011',
          "Device Control 2"          -> '\u0012',
          "Device Control 3"          -> '\u0013',
          "Device Control 4"          -> '\u0014',
          "Negative Acknowledge"      -> '\u0015',
          "Synchronous Idle"          -> '\u0016',
          "End of Transmission Block" -> '\u0017',
          "Cancel"                    -> '\u0018',
          "End of Medium"             -> '\u0019',
          "Substitute"                -> '\u001A',
          "Escape"                    -> '\u001B',
          "Information Separator 4"   -> '\u001C',
          "Information Separator 3"   -> '\u001D',
          "Information Separator 2"   -> '\u001E',
          "Information Separator 1"   -> '\u001F',
          "Delete"                    -> '\u007F'
        ) foreach { case (name, c) =>
          name in {
            val Left(Parser.ErrorWithInput(str, pos, expect)) =
              comment.parse(s"# $c <- $name"): @unchecked

            str shouldBe s"# $c <- $name"
            pos shouldBe 0
          }
        }
      }
    }
  }
}
