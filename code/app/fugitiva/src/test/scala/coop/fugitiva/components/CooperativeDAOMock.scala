package coop.fugitiva.components

import cats.ApplicativeError
import cats.data.Validated
import cats.effect.{Async, Ref}
import cats.implicits.*

import coop.fugitiva.domain.{Cooperative, CooperativeId}
import coop.fugitiva.util.{RecordNotFound, found}

object CooperativeDAOMock:
  def empty[F[_]](using f: Async[F]): F[CooperativeDAOMock[F]] =
    Ref.of(Set.empty[Cooperative]).map(CooperativeDAOMock.apply)

case class CooperativeDAOMock[F[_]](
    data: Ref[F, Set[Cooperative]]
)(using f: Async[F])
    extends CooperativeDAO[F]:
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
