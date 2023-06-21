package coop.fugitiva.util

import scala.concurrent.Future

import cats.effect.{Async, Sync}

given futureToIO[F[_], A](using f: Async[F]): Conversion[Future[A], F[A]] =
  future => f.fromFuture(f.delay(future))
