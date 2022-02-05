package multiformats.parser

import multiformats.UnsignedVarInt
import parser.bytearray.ByteArrayParser
import parser.bytearray.Cond
import util.types.NonEmptyByteArray

val UnsignedVarIntParser: ByteArrayParser<UnsignedVarInt> =
    (Cond { it.toUByte() >= 128u }.rep() seq Cond { it.toUByte() < 128u })
        .map { (it.first + it.second).toByteArray() }
        .map { NonEmptyByteArray(it) }
        .map { UnsignedVarInt(it) }