package coop.fugitiva

import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s.*
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Server
import org.http4s.server.middleware.Logger

// Middleware

val app: HttpApp[IO] =
  Logger.httpApp(true, true)(rootRouter)

// Server

val server: Resource[IO, Server] =
  EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(app)
    .build

// Entrypoint

object Backend extends IOApp.Simple:
  def run: IO[Unit] = server.useForever
