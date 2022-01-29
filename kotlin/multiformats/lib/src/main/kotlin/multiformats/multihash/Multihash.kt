package multiformats.multihash

import multiformats.UnsignedVarInt
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

    val bytes: ByteArray by lazy {
        UnsignedVarInt(algorithm.code).bytes + UnsignedVarInt(size).bytes + digest
    }
}
