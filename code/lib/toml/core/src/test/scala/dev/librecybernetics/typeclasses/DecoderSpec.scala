package dev.librecybernetics.typeclasses

import scala.language.implicitConversions

import cats.ApplicativeError
import cats.data.Validated
import cats.implicits.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.types.TOML
import dev.librecybernetics.types.toml.given

class DecoderSpec extends AnyWordSpec {
  "Decoder" should {
    "definable" in {
      case class User(name: String, email: String)

      given decoderUser: Decoder[User, TOML.Map] with
        def decode[F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]](toml: TOML.Map): F[User] =
          val result: Validated[Set[Decoder.Error], User] = (
            Validated.fromOption(
              toml.map.get("name").collect[String] { case s: TOML.String => s },
              Set(Decoder.Error.InvalidInput("no name"))
            ),
            Validated.fromOption(
              toml.map.get("email").collect[String] { case s: TOML.String => s },
              Set(Decoder.Error.InvalidInput("no email"))
            )
          ).mapN(User.apply)
          ApplicativeError.apply.fromValidated(result)
    }
  }
}
