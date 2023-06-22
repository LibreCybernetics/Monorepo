package coop.fugitiva

import cats.effect.IO
import cats.syntax.all.*
import org.http4s.dsl.io.*
import org.http4s.{HttpRoutes, Response}
import org.http4s.implicits.*
import org.http4s.scalatags.scalatagsEncoder
import scalatags.Text.all.*

import coop.fugitiva.components.*
import coop.fugitiva.domain.*

val indexRoute = HttpRoutes.of[IO] { case GET -> Root =>
  Ok("Hello world.")
}

val cooperativeRoute = HttpRoutes.of[IO] { case GET -> Root / "cooperative" / IntVar(id) =>
  for cooperative <- CooperativeRepository.PostgresJAsync[IO]().getCooperative(CooperativeId(id))
  yield cooperative match
    case Right(cooperative) =>
      Response.apply(
        body = scalatagsEncoder.toEntity(CooperativeView(cooperative)).body
      )
    case Left(err)          =>
      Response.apply(
        status = NotFound,
        body = scalatagsEncoder.toEntity(html(body(err.toString))).body
      )
}

val rootRouter = (indexRoute <+> cooperativeRoute).orNotFound
