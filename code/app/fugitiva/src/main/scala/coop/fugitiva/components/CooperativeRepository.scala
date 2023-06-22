package coop.fugitiva.components

import cats.ApplicativeError
import cats.data.Validated
import cats.effect.{Async, IOApp}
import cats.implicits.*
import io.getquill.*
import io.getquill.context.Context
import io.getquill.idiom.Idiom

import coop.fugitiva.domain.*
import coop.fugitiva.util.{RecordNotFound, found, futureToAsync}

trait CooperativeRepository[F[_]: Async]:
  def getCooperatives: F[Seq[Cooperative]]
  def getCooperative[E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[CooperativeId]]](
      id: CooperativeId
  ): F[E[Cooperative]]
  def getCooperative[E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[String]]](
      name: String
  ): F[E[Cooperative]]
end CooperativeRepository

object CooperativeRepository:
  private object Queries:
    val getCooperatives: Quoted[EntityQuery[Cooperative]] =
      quote(query[Cooperative])

    def getCooperative[
        D <: Idiom,
        N <: NamingStrategy
    ](
        id: CooperativeId
    )(using
        ctx: Context[D, N]
    ): Quoted[EntityQuery[Cooperative]] =
      quote(getCooperatives.filter(_.id == ctx.lift(id)))

    def getCooperative[
        D <: Idiom,
        N <: NamingStrategy
    ](
        url: String
    )(using
        ctx: Context[D, N]
    ): Quoted[EntityQuery[Cooperative]] =
      quote(getCooperatives.filter(_.url == ctx.lift(url)))

  case class PostgresJAsync[F[_]]()(using
      ctx: PostgresJAsyncContext[SnakeCase],
      f: Async[F]
  ) extends CooperativeRepository[F]:
    import ctx.*

    override def getCooperatives: F[Seq[Cooperative]] =
      f.executionContext.flatMap { implicit ec =>
        ctx.run(Queries.getCooperatives)
      }

    override def getCooperative[
        E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[CooperativeId]]
    ](
        id: CooperativeId
    ): F[E[Cooperative]] =
      f.executionContext.flatMap { implicit ec =>
        ctx.run(Queries.getCooperative(id)).map(_.found(id))
      }

    override def getCooperative[E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[String]]](
        url: String
    ): F[E[Cooperative]] =
      f.executionContext.flatMap { implicit ec =>
        ctx.run(Queries.getCooperative(url)).map(_.found(url))
      }
  end PostgresJAsync
end CooperativeRepository
