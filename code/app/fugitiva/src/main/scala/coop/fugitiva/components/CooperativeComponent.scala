package coop.fugitiva.components

import scala.concurrent.ExecutionContext.Implicits.global

import cats.effect.{IO, IOApp}
import io.getquill.*

import coop.fugitiva.domain.*

trait CooperativeComponent {
  def getCooperatives: IO[Seq[Cooperative]]
  def getCooperative(id: CooperativeId): IO[Option[Cooperative]]
}

object CooperativeComponentProd extends CooperativeComponent {
  val ctx = PostgresJAsyncContext(SnakeCase, "quill")
  import ctx.*

  override def getCooperatives: IO[Seq[Cooperative]] =

    inline def cooperatives = query[Cooperative]

    IO.fromFuture(IO(run(cooperatives)))

  override def getCooperative(id: CooperativeId): IO[Option[Cooperative]] =
    IO.fromFuture(
      IO(
        run(
          query[Cooperative].filter(_.id == lift(id))
        ).map(_.headOption)
      )
    )
}

object CooperativeComponentTest extends IOApp.Simple {
  override def run: IO[Unit] =
    for
      _ <- IO(println("Running test"))
      _ <- IO(println("Cooperatives:"))
      _ <- CooperativeComponentProd.getCooperatives.map(_.foreach(println))
      _ <- IO(println("Cooperative 1:"))
      _ <- CooperativeComponentProd.getCooperative(1).map(println)
      _ <- IO(println("Cooperative 2:"))
      _ <- CooperativeComponentProd.getCooperative(2).map(println)
    yield ()
}