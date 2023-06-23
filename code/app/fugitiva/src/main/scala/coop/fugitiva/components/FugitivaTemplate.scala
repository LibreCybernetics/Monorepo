package coop.fugitiva.components

import scalatags.Text
import scalatags.Text.all.*

object FugitivaTemplate {
  private def cssImport(url: String, hash: String): Frag =
    link(
      rel := "stylesheet",
      href := url,
      integrity := hash,
      crossorigin := "anonymous"
    )

  def apply(subtitle: Option[String], content: Frag): Frag =
    html(
      head(
        tag("title")("Fugitiva" + subtitle.map(" — " + _).getOrElse("")),
        cssImport(
          "https://cdn.jsdelivr.net/npm/purecss@3.0.0/build/pure-min.css",
          "sha384-X38yfunGUhNzHpBaEBsWLO+A0HDYOQi8ufWDkZ0k9e0eXz/tH3II7uKZ9msv++Ls",
        )
      ),
      body(
        div(
          cls := "pure-menu pure-menu-horizontal",
          pre("Fugitiva"),
          ul(
            cls := "pure-menu-list",
            li(
              cls := "pure-menu-item",
                a(
                    cls := "pure-menu-link",
                    href := "/",
                    "Home"
                )
            ),
            li(
              cls := "pure-menu-item",
              a(
                cls := "pure-menu-link",
                href := "/cooperativas",
                "Cooperativas"
              )
            )
          )
        ),
        content
      )
    )
}
