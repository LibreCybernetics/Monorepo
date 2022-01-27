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

val decoder = Base64.getDecoder()

@kotlin.ExperimentalUnsignedTypes
class UnsignedVarIntTest {
    @Test
    fun exampleValuesFromSpec() {
        val testVectors: List<Pair<BigInteger, ByteArray>> =
            File("../../../spec/multiformats/unsigned-varint-examples.csv")
                .readLines()
                .map { it.split(',') }
                .map { Pair(it.component1().trim(), it.component2().trim()) }
                .map { (int, bytes) -> Pair(int.toBigInteger(), decoder.decode(bytes)) }

        testVectors.forEach { (int, bytes) ->
            assertContentEquals(bytes, UnsignedVarInt(int).bytes)
            assertEquals(int, UnsignedVarInt(bytes).int)
        }
    }

    @Test
    fun randomValues() {
        for (i in 1..1000) {
            val cieling = min(Long.MAX_VALUE, UnsignedVarInt.PRACTICAL_MAX.toLong())
            val random = Random.nextLong(cieling)
            assertEquals(
                random,
                UnsignedVarInt(UnsignedVarInt(random.toBigInteger()).bytes).int.toLong()
            )
        }
    }

    @Test
    fun throwsErrors() {
        // Negatives are not valid
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt.digits(-1.toBigInteger())
        }
        assertFailsWith(IllegalArgumentException::class) { UnsignedVarInt(-1.toBigInteger()) }
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt.encode(-1.toBigInteger())
        }

        // PRACTICAL_MAX is tipping point
        UnsignedVarInt(UnsignedVarInt.PRACTICAL_MAX)
        UnsignedVarInt(UnsignedVarInt(UnsignedVarInt.PRACTICAL_MAX).bytes)
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt(UnsignedVarInt.PRACTICAL_MAX + 1.toBigInteger())
        }
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt.digits(UnsignedVarInt.PRACTICAL_MAX + 1.toBigInteger())
        }
        assertFailsWith(IllegalArgumentException::class) {
            UnsignedVarInt.encode(UnsignedVarInt.PRACTICAL_MAX + 1.toBigInteger())
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
