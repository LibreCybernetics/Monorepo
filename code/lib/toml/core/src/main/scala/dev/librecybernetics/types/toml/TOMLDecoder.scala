package dev.librecybernetics.types.toml

import scala.deriving.Mirror
import scala.compiletime.*

import cats.{Applicative, ApplicativeError}
import cats.data.Validated
import cats.syntax.all.*

import dev.librecybernetics.types.TOML
import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.types.toml.decodeField

/*

References:
 - https://docs.scala-lang.org/scala3/reference/contextual/derivation.html
 - https://blog.philipp-martini.de/blog/magic-mirror-scala3/
 */

type TOMLDecoder[A] = Decoder[A, TOML]

object TOMLDecoder:
  inline def decoderProduct[T](
      p: Mirror.ProductOf[T],
      labelsAndInstances: => List[(String, TOMLDecoder[?])]
  ): Decoder[T, TOML] =
    new Decoder[T, TOML]:
      def decode[
          F[+_]: [M[_]] =>> ApplicativeError[M, Set[Decoder.Error]]
      ](
          toml: TOML
      ): F[T] =
        toml match
          case toml: TOML.Map =>
            val decodedFields: List[Validated[Set[Decoder.Error], Any]] =
              labelsAndInstances.map { case (label, instance) =>
                toml.decodeField(label)(using instance)
              }

            val decodedProduct = decodedFields.sequence.map { (values: Seq[Any]) =>
              val product: Product = new Product {
                override def productArity: Int            = labelsAndInstances.length
                override def productElement(n: Int): Any  = values(n)
                override def canEqual(that: Any): Boolean = false
              }

              p.fromProduct(product)
            }

            ApplicativeError.apply.fromValidated(decodedProduct)
          case _              =>
            ApplicativeError.apply
              .raiseError(Set(Decoder.Error.InvalidType(toml.getClass.getSimpleName)))

  inline def getElemLabels[A <: Tuple]: List[String] = inline erasedValue[A] match
    case _: EmptyTuple     => Nil
    case _: (head *: tail) =>
      val headElementLabel  = constValue[head].toString
      val tailElementLabels = getElemLabels[tail]
      headElementLabel :: tailElementLabels

  inline def summonInstances[T, Elems <: Tuple]: List[TOMLDecoder[?]] =
    inline erasedValue[Elems] match
      case _: (elem *: elems) => summonOne[T, elem] :: summonInstances[T, elems]
      case _: EmptyTuple      => Nil

  inline def summonOne[T, Elem]: TOMLDecoder[Elem] =
    inline erasedValue[Elem] match
      case _ => summonInline[TOMLDecoder[Elem]]

  inline def derived[T](using m: Mirror.Of[T]): Decoder[T, TOML] =
    val labels             = getElemLabels[m.MirroredElemLabels]
    val elemInstances      = summonInstances[T, m.MirroredElemTypes]
    val labelsAndInstances = labels.zip(elemInstances)

    inline m match
      case s: Mirror.SumOf[T]     => ??? // decoderSum(s, elemInstances)
      case p: Mirror.ProductOf[T] => decoderProduct(p, labelsAndInstances)
