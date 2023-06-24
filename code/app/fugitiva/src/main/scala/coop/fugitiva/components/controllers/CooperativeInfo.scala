package coop.fugitiva.components.controllers

import cats.effect.IO
import coop.fugitiva.components.repository.CooperativeRepository
import coop.fugitiva.components.views.{CooperativeView, CooperativesView}
import io.getquill.{PostgresJAsyncContext, SnakeCase}
import org.http4s.dsl.io.{->, /, GET, NotFound, Root}
import org.http4s.headers.`Content-Type`
import org.http4s.scalatags.scalatagsEncoder
import org.http4s.{Charset, Headers, HttpRoutes, MediaType, Response}
import scalatags.Text.all.*

case class CooperativeInfo()(using ctx: PostgresJAsyncContext[SnakeCase]):
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
