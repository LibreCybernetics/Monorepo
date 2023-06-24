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

val cooperativeRoute = HttpRoutes.of[IO] {
  case GET -> Root / "cooperativas"      =>
    for cooperatives <- CooperativeRepository.PostgresJAsync[IO]().getCooperatives
    yield Response(
      body = scalatagsEncoder.toEntity(CooperativesView(cooperatives.toSet)).body,
      headers = Headers(
        // TODO: add automagically?
        `Content-Type`(MediaType.text.html, Charset.`UTF-8`)
      )
    )
  case GET -> Root / "cooperativa" / url =>
    for cooperative <- CooperativeRepository.PostgresJAsync[IO]().getCooperative(url)
    yield cooperative match
      case Right(cooperative) =>
        Response(
          body = scalatagsEncoder.toEntity(CooperativeView(cooperative)).body,
          headers = Headers(
            // TODO: add automagically?
            `Content-Type`(MediaType.text.html, Charset.`UTF-8`)
          )
        )
      case Left(err)          =>
        Response(
          status = NotFound,
          body = scalatagsEncoder.toEntity(html(body(err.toString))).body
        )
}

val rootRouter = (indexRoute <+> cooperativeRoute).orNotFound
