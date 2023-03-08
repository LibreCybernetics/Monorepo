package dev.librecybernetics.parser.toml

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.types.TOML

class CommentSpec extends AnyWordSpec {
  "Comment" when {
    "Valid comment" should {
      "Parse Simple" in {
        val Right(("", r)) = comment.parse("# test") : @unchecked
        r shouldEqual TOML.Comment("test")
      }

      "Parse Multiline" in {
        val Right(("", r)) = comment.parse(
          """# test
            |# multi-line""".stripMargin): @unchecked
        r shouldEqual TOML.Comment("test\nmulti-line")
      }
    }
  }
}
