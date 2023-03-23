package dev.librecybernetics.types.toml

import cats.Show

import dev.librecybernetics.typeclasses.Decoder
import dev.librecybernetics.types.TOML

given (using s: Show[BigInt]): Show[TOML.Integer] with
  override def show(x: TOML.Integer): String = s.show(x.bigInt)

// Byte

given encodeByteConversion: Conversion[Byte, TOML.Integer] with
  override def apply(x: Byte): TOML.Integer = TOML.Integer(BigInt(x))

given decodeByteConversion: Decoder[Byte, TOML.Integer] with
  override def decode(x: TOML.Integer): Byte = x.bigInt.toByte


// Short

given encodeShortConversion: Conversion[Short, TOML.Integer] with
  override def apply(x: Short): TOML.Integer = TOML.Integer(BigInt(x))
  
given decodeShortConversion: Decoder[Short, TOML.Integer] with
  override def decode(x: TOML.Integer): Short = x.bigInt.toShort



// Int

given encodeIntConversion: Conversion[Int, TOML.Integer] with
  override def apply(x: Int): TOML.Integer = TOML.Integer(BigInt(x))

given decodeIntConversion: Decoder[Int, TOML.Integer] with
  override def decode(x: TOML.Integer): Int = x.bigInt.toInt



// Long

given encodeLongConversion: Conversion[Long, TOML.Integer] with
  override def apply(x: Long): TOML.Integer = TOML.Integer(BigInt(x))

given decodeLongConversion: Decoder[Long, TOML.Integer] with
  override def decode(x: TOML.Integer): Long = x.bigInt.toLong



// BigInt

given encodeBigIntConversion: Conversion[BigInt, TOML.Integer] with
  override def apply(x: BigInt): TOML.Integer = TOML.Integer(x)

given decodeBigIntConversion: Decoder[BigInt, TOML.Integer] with
  override def decode(x: TOML.Integer): BigInt = x.bigInt

