package dev.librecybernetics.types.toml

import cats.implicits.*
import cats.Semigroup

import dev.librecybernetics.types.TOML

private def canCombine(x: TOML, y: TOML): Boolean =
  (x, y) match
    case (TOML.Array(_), TOML.Array(_))   => true
    case (TOML.Map(mapX), TOML.Map(mapY)) =>
      (mapX.keySet intersect mapY.keySet).forall { commonKey =>
        canCombine(mapX(commonKey), mapY(commonKey))
      }
    case _                                => false

private def combineNested(x: TOML, y: TOML): TOML =
  (x, y) match
    case (x @ TOML.Array(_), y @ TOML.Array(_)) =>
      x combine y

    case (x @ TOML.Map(_), y @ TOML.Map(_)) if canCombine(x, y) =>
      x combine y

    case (_, _) =>
      throw new IllegalArgumentException("cant combine unu")
  end match

given Semigroup[TOML.Array] with
  override def combine(x: TOML.Array, y: TOML.Array): TOML.Array =
    (x, y) match
      case (TOML.Array(arrX), TOML.Array(arrY)) => TOML.Array(arrX ++ arrY)

given Semigroup[TOML.Map] with
  override def combine(x: TOML.Map, y: TOML.Map): TOML.Map =
    (x, y) match
      case (TOML.Map(mapX), TOML.Map(mapY)) =>
        val commonKeys = mapX.keySet intersect mapY.keySet

        require(commonKeys forall { key => canCombine(mapX(key), mapY(key)) })

        TOML.Map(
          (mapX ++ mapY) ++ (commonKeys.collect { key =>
            key -> combineNested(mapX(key), mapY(key))
          })
        )
