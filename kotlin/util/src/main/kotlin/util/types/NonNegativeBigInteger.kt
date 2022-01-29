package util.types

import java.math.BigInteger

@JvmInline
value class NonNegativeBigInteger(val int: BigInteger) {
    init {
		require(int >= BigInteger.ZERO)
	}

	fun longValueExact() = int.longValueExact()
}
