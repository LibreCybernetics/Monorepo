package coop.fugitiva.components

import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import coop.fugitiva.components.repository.CooperativeRepository
import org.scalatest.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.*
import coop.fugitiva.domain.Cooperative

class CooperativeRepositorySpec extends AsyncWordSpec with AsyncIOSpec {
  val cooperativeDAO: CooperativeRepository.PostgresJAsync[IO] =
    CooperativeRepository.PostgresJAsync[IO]()(using coop.fugitiva.PostgresContext)

  "CooperativeDAO" when {
    "Query" should {
      "all" in {
        cooperativeDAO.getCooperatives.asserting {
          _ should contain(Cooperative(1, "Autodefensa Alimentaria"))
        }
      }

      "by id" in {
        cooperativeDAO.getCooperative(1).asserting {
          _ shouldEqual Right(Cooperative(1, "Autodefensa Alimentaria"))
        }
      }

      "by name" in {
        cooperativeDAO.getCooperative("Autodefensa Alimentaria").asserting {
          _ shouldEqual Right(Cooperative(1, "Autodefensa Alimentaria"))
        }
      }
    }
  }
}
