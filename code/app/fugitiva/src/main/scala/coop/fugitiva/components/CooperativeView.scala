package coop.fugitiva.components

import scalatags.Text.all.*

import coop.fugitiva.domain.Cooperative

object CooperativeView:
  def apply(cooperative: Cooperative): Frag =
    html(
      head(),
      body(cooperative.toString)
    )

end CooperativeView
