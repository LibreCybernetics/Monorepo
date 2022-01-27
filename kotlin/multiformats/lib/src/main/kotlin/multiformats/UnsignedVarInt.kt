package multiformats

import java.math.BigInteger

data class UnsignedVarInt(val int: BigInteger) {
    companion object {
        const val PRACTICAL_MAX_BYTES: Int = 9
        val PRACTICAL_MAX: BigInteger =
            128.toBigInteger().pow(PRACTICAL_MAX_BYTES) - 1.toBigInteger()

        @JvmStatic
        fun digits(int: BigInteger): Int {
            require(int >= BigInteger.ZERO)
            require(int <= PRACTICAL_MAX)

            var exp: Int = 0
            while (128.toBigInteger().pow(exp) - 1.toBigInteger() < int) exp += 1

            return exp
        }

        @JvmStatic
        fun encode(int: BigInteger): ByteArray {
            require(int >= BigInteger.ZERO)
            require(int <= PRACTICAL_MAX)

            val digits = digits(int)
            return ByteArray(digits) { idx ->
                (
                    int.divide(128.toBigInteger().pow(idx))
                        .mod(128.toBigInteger())
                        .toInt().toUByte() + if (idx == digits - 1) 0u else 128u
                    ).toByte()
            }
        }

        @JvmStatic
        fun decode(bytes: ByteArray): BigInteger {
            require(bytes.size > 0)
            require(bytes.size <= PRACTICAL_MAX_BYTES)
            require(bytes.dropLast(1).all { it.toUByte() >= 128u })
            require(bytes.last().toUByte() < 128u)

            return bytes.mapIndexed { idx, byte ->
                128.toBigInteger().pow(idx) * byte.toUByte().mod(128u).toInt().toBigInteger()
            }.reduce { a, b -> a + b }
        }
    }

    constructor(bytes: ByteArray) : this(decode(bytes))

    init {
        require(int >= BigInteger.ZERO)
        require(int <= PRACTICAL_MAX)
    }

    val bytes: ByteArray by lazy { encode(int) }
}
