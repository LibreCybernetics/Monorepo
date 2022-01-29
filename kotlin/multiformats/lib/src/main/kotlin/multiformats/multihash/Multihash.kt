package multiformats.multihash

import multiformats.UnsignedVarInt
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
        fun decode(bytes: ByteArray): Multihash = TODO()
    }

    val nebytes: NonEmptyByteArray by lazy {
        UnsignedVarInt(algorithm.code).nebytes + UnsignedVarInt(size).nebytes + digest
    }
}
