package coop.fugitiva.components.views

import scalatags.Text.all.*

object IndexView:
  def apply(): Frag =
    FugitivaTemplate(
      None,
      h1("Hola Mundo!")
    )

end IndexView
