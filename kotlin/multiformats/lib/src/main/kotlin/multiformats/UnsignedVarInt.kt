package multiformats

import util.extensions.pow
import util.types.NonNegativeBigInteger

// NOTE: ULong (64-bits) is more than enough to meet current spec of maximum 9 bytes (7-bits)
data class UnsignedVarInt(val int: ULong) {
    companion object {
        const val PRACTICAL_MAX_BYTES: Int = 9
        val PRACTICAL_MAX: ULong = 128.toULong().pow(PRACTICAL_MAX_BYTES) - 1u

        fun digits(int: ULong): Int {
            require(int <= PRACTICAL_MAX)

            var exp = 0
            while ((128.toULong().pow(exp) - 1u) < int) exp += 1

            return exp
        }

        fun encode(int: ULong): ByteArray {
            require(int <= PRACTICAL_MAX)

            val digits = digits(int)
            return ByteArray(digits) { idx -> (
                int.div((128.toULong().pow(idx)))
                    .rem(128u) + if (idx == digits - 1) 0u else 128u
                ).toByte()
            }
        }

        @JvmStatic
        fun decode(bytes: ByteArray): ULong {
            require(bytes.isNotEmpty())
            require(bytes.size <= PRACTICAL_MAX_BYTES)
            require(bytes.dropLast(1).all { it.toUByte() >= 128u })
            require(bytes.last().toUByte() < 128u)

            return bytes.mapIndexed { idx, byte ->
                128.toULong().pow(idx) * byte.toUByte().mod(128u)
            }.reduce { a, b -> a + b }
        }
    }

    constructor(bytes: ByteArray) : this(decode(bytes))
    constructor(nnint: NonNegativeBigInteger) : this(nnint.int.longValueExact().toULong())

    init {
        require(int <= PRACTICAL_MAX)
    }

    val bytes: ByteArray by lazy { encode(int) }
}
