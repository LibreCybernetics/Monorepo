package multiformats.multihash

import multiformats.Multicodec
import multiformats.UnsignedVarInt
import multiformats.UnsignedVarIntParser
import parser.GenericParser
import parser.bytearray.Any

val MultihashBinaryParser: GenericParser<ByteArray, Multihash> =
    (UnsignedVarIntParser seq (UnsignedVarIntParser.flatMap { size ->
        Any.repExactly(size.uint).map { size to it }
    })).map {
        val algorithm = Multicodec.Companion.Multihash.get(it.first.ushort)
        Multihash(algorithm, it.second.first.ushort, it.second.second.toByteArray())
    }