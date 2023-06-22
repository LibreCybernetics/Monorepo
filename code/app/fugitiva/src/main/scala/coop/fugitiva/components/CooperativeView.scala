package coop.fugitiva.components

import scalatags.Text.all.*

import coop.fugitiva.domain.Cooperative

object CooperativeView:
  def apply(cooperative: Cooperative): Frag =
    FugitivaTemplate(
      Some(cooperative.name),
      h1(cooperative.name),
      cooperative.byline.fold(div())(h2(_))
    )

end CooperativeView