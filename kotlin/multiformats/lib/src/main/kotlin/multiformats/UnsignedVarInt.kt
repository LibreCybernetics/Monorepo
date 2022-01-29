package multiformats

import java.math.BigInteger
import util.types.NonNegativeBigInteger

data class UnsignedVarInt(val nnint: NonNegativeBigInteger) {
    companion object {
        const val PRACTICAL_MAX_BYTES: Int = 9
        val PRACTICAL_MAX: NonNegativeBigInteger =
            NonNegativeBigInteger(128.toBigInteger().pow(PRACTICAL_MAX_BYTES) - 1.toBigInteger())

        @JvmStatic
        fun digits(nnint: NonNegativeBigInteger): Int {
            require(nnint.int <= PRACTICAL_MAX.int)

            var exp: Int = 0
            while (128.toBigInteger().pow(exp) - 1.toBigInteger() < nnint.int) exp += 1

            return exp
        }

        @JvmStatic
        fun encode(nnint: NonNegativeBigInteger): ByteArray {
            require(nnint.int <= PRACTICAL_MAX.int)

            val digits = digits(nnint)
            return ByteArray(digits) { idx ->
                (
                    nnint.int.divide(128.toBigInteger().pow(idx))
                        .mod(128.toBigInteger())
                        .toInt().toUByte() + if (idx == digits - 1) 0u else 128u
                    ).toByte()
            }
        }

        @JvmStatic
        fun decode(bytes: ByteArray): NonNegativeBigInteger {
            require(bytes.size > 0)
            require(bytes.size <= PRACTICAL_MAX_BYTES)
            require(bytes.dropLast(1).all { it.toUByte() >= 128u })
            require(bytes.last().toUByte() < 128u)

            val result = bytes.mapIndexed { idx, byte ->
                128.toBigInteger().pow(idx) * byte.toUByte().mod(128u).toInt().toBigInteger()
            }.reduce { a, b -> a + b }

            return NonNegativeBigInteger(result)
        }
    }

    constructor(bytes: ByteArray) : this(decode(bytes))

    init {
        require(nnint.int <= PRACTICAL_MAX.int)
    }

    val bytes: ByteArray by lazy { encode(nnint) }
}
