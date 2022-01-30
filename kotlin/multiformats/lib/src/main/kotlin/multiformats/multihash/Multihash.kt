package multiformats.multihash

import multiformats.UnsignedVarInt
import parser.ParserError
import parser.ParserSuccess
import util.types.NonEmptyByteArray
import multiformats.Multicodec.Companion.Multihash as MCMultihash

data class Multihash(
    val algorithm: MCMultihash,
    val size: UShort,
    val digest: ByteArray
) {
    init {
        require(digest.size.toUShort() == size)
    }

    companion object {
        fun decode(bytes: ByteArray): Multihash {
            return when(val result = MultihashBinaryParser.parse(bytes)) {
                is ParserSuccess -> result.output
                is ParserError -> throw IllegalArgumentException(result.toString())
            }
        }
    }

    val nebytes: NonEmptyByteArray by lazy {
        UnsignedVarInt(algorithm.code).nebytes + UnsignedVarInt(size).nebytes + digest
    }
}
