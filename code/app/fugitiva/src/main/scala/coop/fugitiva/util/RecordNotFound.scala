package coop.fugitiva.util

import cats.{ApplicativeError, Semigroup}
import cats.data.Validated

case class RecordNotFound[+Key](key: Key) extends Exception
extension [A, Key](o: Option[A])
  def found[F[_]](key: Key)(using f: ApplicativeError[F, RecordNotFound[Key]]): F[A] = o match
    case None    => f raiseError RecordNotFound(key)
    case Some(x) => f pure x

extension [A, Key](s: Seq[A])
  def found[F[_]](key: Key)(using f: ApplicativeError[F, RecordNotFound[Key]]): F[A] = s match
    case Nil      => f raiseError RecordNotFound(key)
    case x :: Nil => f pure x
