package dev.librecybernetics.typeclasses

import scala.language.implicitConversions

import cats.ApplicativeError
import cats.data.Validated
import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.{*, given}

class DecoderSpec extends AnyWordSpec {
  "Decoder" should {
    "definable" in {
      case class User(name: String, email: String)

      given decoderUser: Decoder[User, TOML.Map] with
        def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](toml: TOML.Map): F[User] =
          ApplicativeError.apply.fromValidated((
            toml
              .getField("name")
              .andThen { _.decodeString[String] },
            toml
              .getField("email")
              .andThen { _.decodeString[String] }
          ).mapN(User.apply))

      val toml: TOML.Map = TOML.Map(Map(
          "name" -> "John Doe",
          "email" -> "john@example.com"
        )
      )

      toml.decodeMap[User] match
        case Validated.Valid(user) => user shouldEqual User("John Doe", "john@example.com")
        case Validated.Invalid(errors) => fail(s"Errors: $errors")
    }
  }
}
