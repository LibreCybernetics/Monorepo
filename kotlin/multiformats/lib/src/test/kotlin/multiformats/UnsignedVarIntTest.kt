package multiformats

import util.types.NonNegativeBigInteger
import java.io.File
import java.util.*
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

val decoder = Base64.getDecoder()

class UnsignedVarIntTest {
    @Test
    fun exampleValuesFromSpec() {
        val testVectors: List<Pair<NonNegativeBigInteger, ByteArray>> =
            File("../../../spec/multiformats/unsigned-varint-examples.csv")
                .readLines()
                .map { it.split(',') }
                .map { Pair(it.component1().trim(), it.component2().trim()) }
                .map { (int, bytes) -> Pair(NonNegativeBigInteger(int.toBigInteger()), decoder.decode(bytes)) }

        testVectors.forEach { (nnint, bytes) ->
            assertContentEquals(bytes, UnsignedVarInt(nnint).bytes)
            assertEquals(nnint.int, UnsignedVarInt(bytes).int.toLong().toBigInteger())
        }
    }

    @Test
    fun randomValues() {
        for (i in 1..1000) {
            val cieling = min(Long.MAX_VALUE, UnsignedVarInt.PRACTICAL_MAX.toLong())
            val random = Random.nextLong(cieling).toULong()
            assertEquals(
                random,
                UnsignedVarInt(UnsignedVarInt(random).bytes).int
            )
        }
    }

    @Test
    @ExperimentalUnsignedTypes
    fun throwsErrors() {
        // PRACTICAL_MAX is tipping point
        UnsignedVarInt(UnsignedVarInt.PRACTICAL_MAX)
        UnsignedVarInt(UnsignedVarInt(UnsignedVarInt.PRACTICAL_MAX).bytes)
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt(UnsignedVarInt.PRACTICAL_MAX + 1u)
        }
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt.digits(UnsignedVarInt.PRACTICAL_MAX + 1u)
        }
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt.encode(UnsignedVarInt.PRACTICAL_MAX + 1u)
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
