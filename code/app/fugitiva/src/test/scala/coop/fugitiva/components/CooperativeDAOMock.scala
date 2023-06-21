package coop.fugitiva.components

import cats.implicits.*
import cats.effect.{Async, Ref}

import coop.fugitiva.domain.{Cooperative, CooperativeId}

object CooperativeDAOMock:
  def empty[F[_]](using f: Async[F]): F[CooperativeDAOMock[F]] =
    Ref.of(Set.empty[Cooperative]).map(CooperativeDAOMock.apply)

case class CooperativeDAOMock[F[_]](
    data: Ref[F, Set[Cooperative]]
)(using f: Async[F])
    extends CooperativeDAO[F]:
  override def getCooperatives: F[Seq[Cooperative]] =
    data.get.map(_.toSeq)

  override def getCooperative(id: CooperativeId): F[Option[Cooperative]] =
    data.get.map(_.find(_.id == id))

  override def getCooperative(name: String): F[Option[Cooperative]] =
    data.get.map(_.find(_.name == name))
