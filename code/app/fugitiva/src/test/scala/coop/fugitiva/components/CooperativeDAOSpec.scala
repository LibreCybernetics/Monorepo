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
      "cooperatives" in {
        cooperativeDAO.getCooperatives.asserting { cooperatives =>
          cooperatives should contain(Cooperative(1, "Autodefensa Alimentaria"))
        }
      }

      "cooperative" in {
        cooperativeDAO.getCooperative(1).asserting { cooperative =>
          cooperative shouldBe Some(Cooperative(1, "Autodefensa Alimentaria"))
        }
      }
    }
  }
}
