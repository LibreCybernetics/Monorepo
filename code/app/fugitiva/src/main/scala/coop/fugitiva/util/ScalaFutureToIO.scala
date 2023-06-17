package coop.fugitiva.util

import scala.concurrent.Future

import cats.effect.IO

given futureToIO[A]: Conversion[Future[A], IO[A]] = f => IO.fromFuture(IO(f))
