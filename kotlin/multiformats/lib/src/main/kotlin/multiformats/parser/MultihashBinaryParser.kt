package multiformats.parser

import multiformats.Multicodec
import multiformats.multihash.Multihash
import parser.GenericParser
import parser.bytearray.Any
import parser.bytearray.ByteArrayParser

val MultihashParser: GenericParser<ByteArray, Multihash> =
    (UnsignedVarIntParser seq
        (UnsignedVarIntParser.flatMap { size ->
            Any.repExactly(size.int).map { size to it }
        })
    ).map {
        val algorithm = Multicodec.Companion.Multihash.get(it.first.ushort)
        Multihash(algorithm, it.second.first.ushort, it.second.second.toByteArray())
    }