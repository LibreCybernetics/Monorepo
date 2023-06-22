package coop.fugitiva.domain

case class Cooperative(
    id: CooperativeId,
    url: String,
    name: String,
    byline: Option[String],
)
