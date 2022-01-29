package util.types

import java.math.BigInteger
import kotlin.test.*

class NonNegativeBigIntegerTest {
    @Test
    fun throwsErrors() {
        assertFailsWith(IllegalArgumentException::class) {
            NonNegativeBigInteger(-1.toBigInteger())
        }
    }

    @Test
    fun acceptsNonNegative() {
        fun check(int: BigInteger) {
            assertEquals(int, NonNegativeBigInteger(int).int)
        }

        check(BigInteger.ZERO)
        check(0.toBigInteger())
        check(1.toBigInteger())
    }
}
