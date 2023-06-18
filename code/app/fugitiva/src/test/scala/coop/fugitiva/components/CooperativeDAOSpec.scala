package coop.fugitiva.components

import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.*

import coop.fugitiva.domain.Cooperative

class CooperativeDAOSpec extends AsyncWordSpec with AsyncIOSpec {
  val cooperativeDAO = CooperativeDAO.PostgresJAsync()(using coop.fugitiva.PostgresContext)

  "CooperativeDAO" when {
    "Query" should {
      "all" in {
        cooperativeDAO.getCooperatives.asserting {
          _ should contain(Cooperative(1, "Autodefensa Alimentaria"))
        }
      }

      "by id" in {
        cooperativeDAO.getCooperative(1).asserting {
          _ shouldEqual Some(Cooperative(1, "Autodefensa Alimentaria"))
        }
      }

      "by name" in {
        cooperativeDAO.getCooperative("Autodefensa Alimentaria").asserting {
          _ shouldEqual Some(Cooperative(1, "Autodefensa Alimentaria"))
        }
      }
    }
  }
}
