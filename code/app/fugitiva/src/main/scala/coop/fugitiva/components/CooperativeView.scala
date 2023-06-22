package coop.fugitiva.components

import scalatags.Text.all.*

import coop.fugitiva.domain.Cooperative

object CooperativeView:
  def apply(cooperative: Cooperative): Frag =
    FugitivaTemplate(
      Some(cooperative.name),
      pre(cooperative.toString)
    )

end CooperativeView
