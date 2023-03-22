package dev.librecybernetics.types.toml

import cats.Show

import dev.librecybernetics.typeclasses.{Decoder, fromConversion}
import dev.librecybernetics.types.TOML

given (using s: Show[BigInt]): Show[TOML.Integer] with
  override def show(x: TOML.Integer): String = s.show(x.bigInt)

// Byte

given encodeByteConversion: Conversion[Byte, TOML.Integer] with
  override def apply(x: Byte): TOML.Integer = TOML.Integer(BigInt(x))

given decodeByteConversion: Conversion[TOML.Integer, Byte] with
  override def apply(x: TOML.Integer): Byte = x.bigInt.toByte

given Decoder[Byte, TOML.Integer] = fromConversion

// Short

given encodeShortConversion: Conversion[Short, TOML.Integer] with
  override def apply(x: Short): TOML.Integer = TOML.Integer(BigInt(x))
  
given decodeShortConversion: Conversion[TOML.Integer, Short] with
  override def apply(x: TOML.Integer): Short = x.bigInt.toShort

given Decoder[Short, TOML.Integer] = fromConversion


// Int

given encodeIntConversion: Conversion[Int, TOML.Integer] with
  override def apply(x: Int): TOML.Integer = TOML.Integer(BigInt(x))

given decodeIntConversion: Conversion[TOML.Integer, Int] with
  override def apply(x: TOML.Integer): Int = x.bigInt.toInt

given Decoder[Int, TOML.Integer] = fromConversion


// Long

given encodeLongConversion: Conversion[Long, TOML.Integer] with
  override def apply(x: Long): TOML.Integer = TOML.Integer(BigInt(x))

given decodeLongConversion: Conversion[TOML.Integer, Long] with
  override def apply(x: TOML.Integer): Long = x.bigInt.toLong

given Decoder[Long, TOML.Integer] = fromConversion


// BigInt

given encodeBigIntConversion: Conversion[BigInt, TOML.Integer] with
  override def apply(x: BigInt): TOML.Integer = TOML.Integer(x)

given decodeBigIntConversion: Conversion[TOML.Integer, BigInt] with
  override def apply(x: TOML.Integer): BigInt = x.bigInt

given Decoder[BigInt, TOML.Integer] = fromConversion
