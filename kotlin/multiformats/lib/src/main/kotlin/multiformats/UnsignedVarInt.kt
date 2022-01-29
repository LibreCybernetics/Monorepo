package multiformats

import util.extensions.pow
import util.types.NonEmptyByteArray
import util.types.NonNegativeBigInteger

// NOTE: ULong (64-bits) is more than enough to meet current spec of maximum 9 bytes (7-bits)
data class UnsignedVarInt(val ulong: ULong) {
    constructor(nebytes: NonEmptyByteArray) : this(decode(nebytes))
    constructor(ushort: UShort) : this(ushort.toULong())
    constructor(nnint: NonNegativeBigInteger) : this(nnint.longValueExact().toULong())

    init {
        require(ulong <= PRACTICAL_MAX)
    }

    companion object {
        const val PRACTICAL_MAX_BYTES: Int = 9
        val PRACTICAL_MAX: ULong = 128.toULong().pow(PRACTICAL_MAX_BYTES) - 1u

        fun digits(int: ULong): Int {
            require(int <= PRACTICAL_MAX)

            var exp = 0
            while ((128.toULong().pow(exp) - 1u) < int) exp += 1

            return exp
        }

        fun encode(int: ULong): NonEmptyByteArray {
            require(int <= PRACTICAL_MAX)

            val digits = digits(int)
            return NonEmptyByteArray(ByteArray(digits) { idx -> (
                int.div((128.toULong().pow(idx)))
                    .rem(128u) + if (idx == digits - 1) 0u else 128u
                ).toByte()
            })
        }

        @JvmStatic
        fun decode(nebytes: NonEmptyByteArray): ULong {
            require(nebytes.bytes.size <= PRACTICAL_MAX_BYTES)
            require(nebytes.dropLast(1).all { it.toUByte() >= 128u })
            require(nebytes.last().toUByte() < 128u)

            return nebytes.mapIndexed { idx, byte ->
                128.toULong().pow(idx) * byte.toUByte().mod(128u)
            }.reduce { a, b -> a + b }
        }
    }

    val nebytes: NonEmptyByteArray by lazy { encode(ulong) }
    val long: Long by lazy { ulong.toLong() }
    val nnint: NonNegativeBigInteger by lazy { NonNegativeBigInteger(long.toBigInteger()) }
}
