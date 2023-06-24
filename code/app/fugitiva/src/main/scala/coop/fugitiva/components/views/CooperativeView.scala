package coop.fugitiva.components.views

import coop.fugitiva.domain.Cooperative
import scalatags.Text.all.*

object CooperativeView:
  def apply(cooperative: Cooperative): Frag =
    FugitivaTemplate(
      Some(cooperative.name),
      div(
        h1(cooperative.name),
        cooperative.byline.fold(div())(h2(_))
      )
    )

end CooperativeView
