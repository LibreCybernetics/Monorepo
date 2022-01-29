package multiformats

import java.io.File
import java.math.BigInteger
import java.util.Base64
import kotlin.ExperimentalUnsignedTypes
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.*
import kotlin.test.Test
import kotlin.ubyteArrayOf
import util.types.NonNegativeBigInteger

val decoder = Base64.getDecoder()

@kotlin.ExperimentalUnsignedTypes
class UnsignedVarIntTest {
    @Test
    fun exampleValuesFromSpec() {
        val testVectors: List<Pair<NonNegativeBigInteger, ByteArray>> =
            File("../../../spec/multiformats/unsigned-varint-examples.csv")
                .readLines()
                .map { it.split(',') }
                .map { Pair(it.component1().trim(), it.component2().trim()) }
                .map { (int, bytes) -> Pair(NonNegativeBigInteger(int.toBigInteger()), decoder.decode(bytes)) }

        testVectors.forEach { (int, bytes) ->
            assertContentEquals(bytes, UnsignedVarInt(int).bytes)
            assertEquals(int, UnsignedVarInt(bytes).nnint)
        }
    }

    @Test
    fun randomValues() {
        for (i in 1..1000) {
            val cieling = min(Long.MAX_VALUE, UnsignedVarInt.PRACTICAL_MAX.int.toLong())
            val random = NonNegativeBigInteger(Random.nextLong(cieling).toBigInteger())
            assertEquals(
                random,
                UnsignedVarInt(UnsignedVarInt(random).bytes).nnint
            )
        }
    }

    @Test
    fun throwsErrors() {
        // PRACTICAL_MAX is tipping point
        UnsignedVarInt(UnsignedVarInt.PRACTICAL_MAX)
        UnsignedVarInt(UnsignedVarInt(UnsignedVarInt.PRACTICAL_MAX).bytes)
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt(NonNegativeBigInteger(UnsignedVarInt.PRACTICAL_MAX.int + 1.toBigInteger()))
        }
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt.digits(NonNegativeBigInteger(UnsignedVarInt.PRACTICAL_MAX.int + 1.toBigInteger()))
        }
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt.encode(NonNegativeBigInteger(UnsignedVarInt.PRACTICAL_MAX.int + 1.toBigInteger()))
        }

        // Empty nor several nulls are valid
        assertFailsWith(IllegalArgumentException::class) { UnsignedVarInt(byteArrayOf()) }
        for (i in 2..UnsignedVarInt.PRACTICAL_MAX_BYTES) {
            assertFailsWith(IllegalArgumentException::class) { UnsignedVarInt(ByteArray(i) { 0 }) }
        }

        // Last should be less than 128
        for (i in 2..UnsignedVarInt.PRACTICAL_MAX_BYTES) {
            assertFailsWith(IllegalArgumentException::class) {
                UnsignedVarInt(ByteArray(i) { 128u.toByte() })
            }
        }

        // PRACTICAL_MAX_BYTES is tipping point
        UnsignedVarInt(
            (
                UByteArray(UnsignedVarInt.PRACTICAL_MAX_BYTES - 1) { 255u } +
                    ubyteArrayOf(127u)
                ).toByteArray()
        )
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt(
                (
                    UByteArray(UnsignedVarInt.PRACTICAL_MAX_BYTES) { 255u } +
                        ubyteArrayOf(127u)
                    ).toByteArray()
            )
        }
    }
}
