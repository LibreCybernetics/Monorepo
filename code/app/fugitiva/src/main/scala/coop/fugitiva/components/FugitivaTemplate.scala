package coop.fugitiva.components

import scalatags.Text.all.*

object FugitivaTemplate {
  def apply(subtitle: Option[String], bodyModifiers: Modifier*): Frag =
    html(
      head(
        tag("title")("Fugitiva" + subtitle.map(" — " + _).getOrElse("")),
      ),
      body(bodyModifiers*)
    )
}
