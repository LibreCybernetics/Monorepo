package coop.fugitiva.components.controllers

import cats.effect.IO
import coop.fugitiva.components.repository.ProductSpecificationRepository
import io.getquill.*
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`
import org.http4s.scalatags.scalatagsEncoder
import org.http4s.{Charset, Headers, HttpRoutes, MediaType, Response}
import scalatags.Text.all.*

case class ProductSpecificationInfo()(using ctx: PostgresJAsyncContext[SnakeCase]):
  val productSpecificationRoute = HttpRoutes.of[IO] { case GET -> Root / "productos" =>
    for productos <- ProductSpecificationRepository.PostgresJAsync[IO]().getProductSpecifications
    yield Response(
      body = scalatagsEncoder
        .toEntity(
          pre(
            productos.map(_.toString).mkString("\n")
          )
        )
        .body,
      headers = Headers(
        // TODO: add automagically?
        `Content-Type`(MediaType.text.html, Charset.`UTF-8`)
      )
    )
  }
