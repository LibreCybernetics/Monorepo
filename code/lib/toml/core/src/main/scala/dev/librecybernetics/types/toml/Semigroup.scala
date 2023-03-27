package dev.librecybernetics.types.toml

import cats.implicits.*
import cats.Semigroup

import dev.librecybernetics.types.TOML

private def canCombine(x: TOML, y: TOML): Boolean =
  (x, y) match
    case (TOML.ArrayOfTables(_), TOML.ArrayOfTables(_))   => true
    case (TOML.Map(mapX), TOML.Map(mapY)) =>
      (mapX.keySet intersect mapY.keySet).forall { commonKey =>
        canCombine(mapX(commonKey), mapY(commonKey))
      }
    case _                                => false

private def combineNested(x: TOML, y: TOML): TOML =
  (x, y) match
    case (x @ TOML.ArrayOfTables(_), y @ TOML.ArrayOfTables(_)) =>
      x combine y

    case (x @ TOML.Map(_), y @ TOML.Map(_)) if canCombine(x, y) =>
      x combine y

    case (_, _) =>
      throw new IllegalArgumentException("cant combine unu")
  end match

given Semigroup[TOML.ArrayOfTables] with
  override def combine(x: TOML.ArrayOfTables, y: TOML.ArrayOfTables): TOML.ArrayOfTables =
    (x, y) match
      case (TOML.ArrayOfTables(arrX), TOML.ArrayOfTables(arrY)) => TOML.ArrayOfTables(arrX ++ arrY)

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
