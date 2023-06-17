package coop.fugitiva

import cats.effect.IO
import org.http4s.dsl.io.*
import org.http4s.HttpRoutes
import org.http4s.implicits.*

val indexRoute = HttpRoutes.of[IO] { case GET -> Root =>
  Ok("Hello world.")
}

val rootRouter = indexRoute.orNotFound
