package coop.fugitiva.components

import scala.language.implicitConversions

import cats.ApplicativeError
import cats.data.Validated
import cats.effect.{Async, IOApp}
import cats.implicits.*
import io.getquill.*
import io.getquill.context.Context
import io.getquill.idiom.Idiom

import coop.fugitiva.domain.*
import coop.fugitiva.util.{RecordNotFound, found, futureToAsync}

trait CooperativeDAO[F[_]: Async]:
  def getCooperatives: F[Seq[Cooperative]]
  def getCooperative[E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[CooperativeId]]](
      id: CooperativeId
  ): F[E[Cooperative]]
  def getCooperative[E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[String]]](
      name: String
  ): F[E[Cooperative]]
end CooperativeDAO

object CooperativeDAO:
  private object CooperativeQueries:
    val getCooperatives: Quoted[EntityQuery[Cooperative]] =
      quote(query[Cooperative])

    def getCooperative[D <: Idiom, N <: NamingStrategy](id: CooperativeId)(using
        ctx: Context[D, N]
    ): Quoted[EntityQuery[Cooperative]] =
      quote(getCooperatives.filter(_.id == ctx.lift(id)))

    def getCooperative[D <: Idiom, N <: NamingStrategy](name: String)(using
        ctx: Context[D, N]
    ): Quoted[EntityQuery[Cooperative]] =
      quote(getCooperatives.filter(_.name == ctx.lift(name)))

  case class PostgresJAsync[F[_]]()(using
      ctx: PostgresJAsyncContext[SnakeCase],
      f: Async[F]
  ) extends CooperativeDAO[F]:
    import ctx.*

    override def getCooperatives: F[Seq[Cooperative]] =
      f.executionContext.flatMap { implicit ec =>
        ctx.run(CooperativeQueries.getCooperatives)
      }

    override def getCooperative[E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[CooperativeId]]](
        id: CooperativeId
    ): F[E[Cooperative]] =
      f.executionContext.flatMap { implicit ec =>
        ctx.run(CooperativeQueries.getCooperative(id)).map(_.found(id))
      }

    override def getCooperative[E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[String]]](
        name: String
    ): F[E[Cooperative]] =
      f.executionContext.flatMap { implicit ec =>
        ctx.run(CooperativeQueries.getCooperative(name)).map(_.found(name))
      }
  end PostgresJAsync
end CooperativeDAO
