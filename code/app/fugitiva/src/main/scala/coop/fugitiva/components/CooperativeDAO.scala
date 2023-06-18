package coop.fugitiva.components

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

import cats.effect.{IO, IOApp}
import io.getquill.*
import io.getquill.idiom.Idiom
import io.getquill.context.Context

import coop.fugitiva.domain.*
import coop.fugitiva.util.futureToIO

trait CooperativeDAO:
  def getCooperatives: IO[Seq[Cooperative]]
  def getCooperative(id: CooperativeId): IO[Option[Cooperative]]

object CooperativeDAO:
  private object CooperativeQueries:
    val getCooperatives: Quoted[EntityQuery[Cooperative]] =
      quote(query[Cooperative])

    def getCooperative[D <: Idiom, N <: NamingStrategy](id: CooperativeId)(using
        ctx: Context[D, N]
    ): Quoted[EntityQuery[Cooperative]] =
      quote(getCooperatives.filter(_.id == ctx.lift(id)))

  case class PostgresJAsync()(using ctx: PostgresJAsyncContext[SnakeCase]) extends CooperativeDAO:
    import ctx.*

    override def getCooperatives: IO[Seq[Cooperative]] =
      ctx.run(CooperativeQueries.getCooperatives)

    override def getCooperative(id: CooperativeId): IO[Option[Cooperative]] =
      ctx.run(CooperativeQueries.getCooperative(id)).map(_.headOption)
  end PostgresJAsync
end CooperativeDAO
