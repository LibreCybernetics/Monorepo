package coop.fugitiva.components

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

import cats.effect.{Async, IOApp}
import io.getquill.*
import io.getquill.idiom.Idiom
import io.getquill.context.Context

import coop.fugitiva.domain.*
import coop.fugitiva.util.futureToIO

trait CooperativeDAO[F[_]: Async]:
  def getCooperatives: F[Seq[Cooperative]]
  def getCooperative(id: CooperativeId): F[Option[Cooperative]]
  def getCooperative(name: String): F[Option[Cooperative]]
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

  case class PostgresJAsync[F[_]]()(using ctx: PostgresJAsyncContext[SnakeCase], f: Async[F]) extends
    CooperativeDAO[F]:
    import ctx.*

    override def getCooperatives: F[Seq[Cooperative]] =
      ctx.run(CooperativeQueries.getCooperatives)

    override def getCooperative(id: CooperativeId): F[Option[Cooperative]] =
      ctx.run(CooperativeQueries.getCooperative(id)).map(_.headOption)

    override def getCooperative(name: String): F[Option[Cooperative]] =
      ctx.run(CooperativeQueries.getCooperative(name)).map(_.headOption)
  end PostgresJAsync
end CooperativeDAO
