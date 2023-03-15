package dev.librecybernetics.fabric

import dev.librecybernetics.types.TOML
import fabric.*
import fabric.rw.*

object TOMLTestReader:
  private given boolean: Reader[TOML.Boolean] = Reader(
    { case TOML.Boolean(boolean) =>
      Obj("type" -> "bool", "value" -> Str(boolean.toString))
    }
  )

  private given comment: Reader[TOML.Comment] = Reader(
    { case TOML.Comment(comment) =>
      Obj("type" -> "comment", "value" -> Str(comment))
    }
  )

  private given string: Reader[TOML.String]   = Reader[TOML.String](
    { case TOML.String(string) =>
      Obj("type" -> "string", "value" -> Str(string))
    }
  )
  private given integer: Reader[TOML.Integer] = Reader[TOML.Integer](
    { case TOML.Integer(integer) =>
      Obj("type" -> "integer", "value" -> Str(integer.toString))
    }
  )

  private given float: Reader[TOML.Float] = Reader(
    { case TOML.Float(double) =>
      Obj("type" -> "float", "value" -> Str(double.toString))
    }
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
    TOMLTestReader.boolean.asInstanceOf,
    TOMLTestReader.comment.asInstanceOf,
    TOMLTestReader.string.asInstanceOf,
    TOMLTestReader.integer.asInstanceOf,
    TOMLTestReader.float.asInstanceOf,
    TOMLTestReader.array.asInstanceOf,
    TOMLTestReader.keyValue.asInstanceOf,
    TOMLTestReader.map.asInstanceOf
  )

