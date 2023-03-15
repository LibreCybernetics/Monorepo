package dev.librecybernetics.fabric

import fabric.*
import fabric.define.DefType
import fabric.rw.*

import dev.librecybernetics.types.TOML

object TOMLReader:
  private given boolean: Reader[TOML.Boolean] = Reader(
    { case TOML.Boolean(boolean) => Bool(boolean) }
  )

  private given comment: Reader[TOML.Comment] = Reader(
    { case TOML.Comment(string) => Str(string) }
  )

  private given string: Reader[TOML.String]   = Reader[TOML.String](
    { case TOML.String(string) => Str(string) }
  )
  private given integer: Reader[TOML.Integer] = Reader[TOML.Integer](
    { case TOML.Integer(integer) => NumInt(integer.toLong) }
  )

  private given float: Reader[TOML.Float] = Reader(
    { case TOML.Float(double) => NumDec(BigDecimal(double)) }
  )

  private given array: Reader[TOML.Array] = Reader(
    { case TOML.Array(arr) => Arr(arr.map(_.json).toVector) }
  )

  private given keyValue: Reader[TOML.KeyValue] = Reader(
    { case TOML.KeyValue(k, v) => Obj(k -> v.json) }
  )

  private given map: Reader[TOML.Map] = Reader(
    { case TOML.Map(map) => Obj(map.view.mapValues(_.json).toMap) }
  )

  given reader: Reader[TOML] = MultiReader[TOML](
    TOMLReader.boolean.asInstanceOf,
    TOMLReader.comment.asInstanceOf,
    TOMLReader.string.asInstanceOf,
    TOMLReader.integer.asInstanceOf,
    TOMLReader.float.asInstanceOf,
    TOMLReader.array.asInstanceOf,
    TOMLReader.keyValue.asInstanceOf,
    TOMLReader.map.asInstanceOf
  )
