package dev.librecybernetics.types.toml

import cats.implicits.*
import cats.Semigroup

import dev.librecybernetics.types.TOML

def canCombine(x: TOML, y: TOML): Boolean =
  (x, y) match
    case (x @ TOML.Array(_), y @ TOML.Array(_)) => true
    case (x @ TOML.Map(_), y @ TOML.Map(_))     => true
    case _                                      => false

def combineNested(x: TOML, y: TOML): TOML =
  (x, y) match
    case (x @ TOML.Array(_), y @ TOML.Array(_)) => x combine y
    case (x @ TOML.Map(_), y @ TOML.Map(_))     => x combine y
    case (_, _)                                 => throw new IllegalArgumentException("cant combine unu")

given semigroupTOMLArray: Semigroup[TOML.Array] with
  override def combine(x: TOML.Array, y: TOML.Array): TOML.Array =
    (x, y) match
      case (TOML.Array(arrX), TOML.Array(arrY)) => TOML.Array(arrX ++ arrY)

given semigroupTOMLMap: Semigroup[TOML.Map] with
  override def combine(x: TOML.Map, y: TOML.Map): TOML.Map =
    (x, y) match
      case (TOML.Map(mapX), TOML.Map(mapY)) =>
        // TODO: Better understand and detect merging restrictions
        val mapXNonMapKeys = mapX.filterNot(_._2.isInstanceOf[TOML.Map]).keys.toSet
        val mapYNonMapKeys = mapY.filterNot(_._2.isInstanceOf[TOML.Map]).keys.toSet

        val commonNonMapKeys = mapXNonMapKeys intersect mapYNonMapKeys

        require(commonNonMapKeys forall { key => canCombine(mapX(key), mapY(key)) })

        TOML.Map(
          (mapX ++ mapY) ++ (commonNonMapKeys.collect { key =>
            key -> combineNested(mapX(key), mapY(key))
          })
        )
