package coop.fugitiva

import cats.effect.IO
import cats.syntax.all.*
import org.http4s.dsl.io.*
import org.http4s.{Charset, Headers, HttpRoutes, MediaType, Response}
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.http4s.scalatags.scalatagsEncoder
import scalatags.Text.all.*
import coop.fugitiva.components.*
import coop.fugitiva.components.controllers.CooperativeInfo
import coop.fugitiva.components.repository.CooperativeRepository
import coop.fugitiva.components.views.{CooperativeView, CooperativesView, IndexView}
import coop.fugitiva.domain.*

val indexRoute = HttpRoutes.of[IO] { case GET -> Root =>
  IO.pure(
    Response(
      body = scalatagsEncoder.toEntity(IndexView()).body,
      headers = Headers(
        // TODO: add automagically?
        `Content-Type`(MediaType.text.html, Charset.`UTF-8`)
      )
    )
  )
}

val rootRouter = (indexRoute <+> CooperativeInfo().cooperativeRoute).orNotFound
