package coop.fugitiva.components.views

import coop.fugitiva.domain.Cooperative
import scalatags.Text.all.*

object CooperativesView:
  def apply(cooperatives: Set[Cooperative]): Frag = {
    val orderedCooperatives = cooperatives.toSeq.sortBy(_.name)

    FugitivaTemplate(
      Some("Cooperativas"),
      ul(
        (for cooperative <- orderedCooperatives
        yield li(
          a(href := s"/cooperativa/${cooperative.url}")(
            cooperative.name
          )
        ))*
      )
    )
  }
