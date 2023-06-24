package coop.fugitiva.components.repository

import scala.concurrent.ExecutionContext

import cats.effect.*
import cats.syntax.all.*
import coop.fugitiva.domain.*
import coop.fugitiva.util.{RecordNotFound, found, futureToAsync}
import io.getquill.*
import io.getquill.context.Context
import io.getquill.idiom.Idiom

trait ProductSpecificationRepository[F[_]: Async]:
  def getProductSpecifications: F[Seq[ProductSpecificationData]]

object ProductSpecificationRepository:
  private object Queries:
    def getProductSpecifications: Quoted[Query[ProductSpecification]] =
      quote(query[ProductSpecification])
    def getParents[
        D <: Idiom,
        N <: NamingStrategy
    ](
        productSpecificationId: ProductSpecificationId
    )(using
        ctx: Context[D, N]
    ): Quoted[Query[ProductSpecificationInheritance]] =
      quote(
        query[ProductSpecificationInheritance]
          .filter(_.id == ctx.lift(productSpecificationId))
      )

  case class PostgresJAsync[F[_]]()(using
      ctx: PostgresJAsyncContext[SnakeCase],
      f: Async[F]
  ) extends ProductSpecificationRepository[F]:
    import ctx.*

    override def getProductSpecifications: F[Seq[ProductSpecificationData]] =
      f.executionContext.flatMap { implicit ec: ExecutionContext =>
        for
          allSpecifications      <- run(Queries.getProductSpecifications)
          specificationsWithData <-
            allSpecifications
              .traverse { case spec: ProductSpecification =>
                for parents <- run(Queries.getParents(spec.id))
                yield ProductSpecificationData(
                  spec,
                  allSpecifications
                    .filter(parents.map(_.inheritsFrom) contains _.id)
                    .toSet
                )
              }
        yield specificationsWithData
      }
    end getProductSpecifications
  end PostgresJAsync
end ProductSpecificationRepository
