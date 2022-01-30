package multiformats.multihash

import java.security.MessageDigest

import multiformats.Multicodec.Companion.Multihash as MCMultihash
import multiformats.UnsignedVarInt
import parser.ParserError
import parser.ParserSuccess
import util.types.NonEmptyByteArray

data class Multihash(
    val algorithm: MCMultihash,
    val size: UShort,
    val digest: ByteArray
) {
    init {
        require(size > 0u)
        require(digest.size.toUShort() == size)
    }

    companion object {
        fun decode(bytes: ByteArray): Multihash {
            when (val result = MultihashBinaryParser.parse(bytes)) {
                is ParserSuccess -> {
                    require(result.remaining.isEmpty())
                    return result.output
                }
                is ParserError -> throw IllegalArgumentException(result.toString())
            }
        }

        fun hash(algorithm: MCMultihash, input: ByteArray, size: UShort? = null): Multihash {
            val digest = hasher(algorithm).digest(input)

            if(size != null) require(size > 0u)
            if(size != null) require(size <= digest.size.toUShort())
            val actualSize = size?.toInt() ?: digest.size

            return Multihash(
                algorithm,
                actualSize.toUShort(),
                if(digest.size == actualSize) digest else
                    digest.take(actualSize).toByteArray()
            )
        }

        fun hasher(algorithm: MCMultihash) =
            when (algorithm) {
                MCMultihash.MD5 -> MessageDigest.getInstance("MD5")
                MCMultihash.SHA1 -> MessageDigest.getInstance("SHA-1")
                MCMultihash.SHA2_256 -> MessageDigest.getInstance("SHA-256")
                MCMultihash.SHA2_384 -> MessageDigest.getInstance("SHA-384")
                MCMultihash.SHA2_512 -> MessageDigest.getInstance("SHA-512")
                MCMultihash.SHA3_224 -> MessageDigest.getInstance("SHA3-224")
                MCMultihash.SHA3_256 -> MessageDigest.getInstance("SHA3-256")
                MCMultihash.SHA3_384 -> MessageDigest.getInstance("SHA3-384")
                MCMultihash.SHA3_512 -> MessageDigest.getInstance("SHA3-512")
                else -> TODO()
            }
    }

    fun check(bytes: ByteArray): Boolean =
        hasher(algorithm).digest(bytes).take(size.toInt()) == digest.toList()

val nebytes: NonEmptyByteArray by lazy {
        UnsignedVarInt(algorithm.code).nebytes + UnsignedVarInt(size).nebytes + digest
    }
    val bytes: ByteArray by lazy { nebytes.bytes }
}
