package coop.fugitiva.components

import cats.ApplicativeError
import cats.data.Validated
import cats.effect.{Async, Ref}
import cats.implicits.*
import coop.fugitiva.components.repository.CooperativeRepository
import coop.fugitiva.domain.{Cooperative, CooperativeId}
import coop.fugitiva.util.{RecordNotFound, found}

object CooperativeRepositoryMock:
  def empty[F[_]](using f: Async[F]): F[CooperativeRepositoryMock[F]] =
    Ref.of(Set.empty[Cooperative]).map(CooperativeRepositoryMock.apply)

case class CooperativeRepositoryMock[F[_]](
    data: Ref[F, Set[Cooperative]]
)(using f: Async[F])
    extends CooperativeRepository[F]:
  override def getCooperatives: F[Seq[Cooperative]] =
    data.get.map(_.toSeq)

  override def getCooperative[
      E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[CooperativeId]]
  ](
      id: CooperativeId
  ): F[E[Cooperative]] =
    data.get.map(_.find(_.id == id).found(id))

  override def getCooperative[
      E[_]: [E[_]] =>> ApplicativeError[E, RecordNotFound[String]]
  ](
      name: String
  ): F[E[Cooperative]] =
    data.get.map(_.find(_.name == name).found(name))
