package multiformats.multihash

import multiformats.Multicodec
import multiformats.Multicodec.Companion.Multihash.Companion.MD5
import multiformats.decoder
import kotlin.random.Random
import java.io.File
import java.util.*
import kotlin.test.*

val decoder = Base64.getDecoder()

class MultihashTest {
    @Test
    fun exampleValuesFromSpec() {
        val testVectors: List<Pair<ByteArray, ByteArray>> =
                File("../../../spec/multiformats/multihash.csv")
                        .readLines()
                        .map { it.split(',') }
                        .map { Pair(it.component1().trim(), it.component2().trim()) }
                        .map { (input, multihash) -> decoder.decode(input) to multiformats.multihash.decoder.decode(multihash) }

        testVectors.forEach { (input, encoded) ->
            // Should be able to read correctly even with
            val multihash = Multihash.decode(encoded)
            assertFailsWith(IllegalArgumentException::class) {
                Multihash.decode(encoded + Random.nextBytes(10))
            }

            // Encoding should match
            assertContentEquals(encoded, multihash.bytes)
            assertTrue(multihash.check(input))
        }
    }

    @Test
    fun randomValues() {
        for (i in 1..1000) {
            val length = Random.nextInt(200)
            val input = Random.nextBytes(length)
            val wrong = Random.nextBytes(length + 1)
            Multicodec.Companion.Multihash.getAlgorithms().forEach {
                val encoded = Multihash.hash(it, input).bytes
                val decoded = Multihash.decode(encoded)
                assertTrue(decoded.check(input))
                assertFalse(decoded.check(wrong))
            }
        }
    }

    @Test
    fun randomValuesShorterSize() {
        for (i in 1..1000) {
            val length = Random.nextInt(200)
            val input = Random.nextBytes(length)
            val wrong = Random.nextBytes(length + 1)
            Multicodec.Companion.Multihash.getAlgorithms().forEach {
                val encoded = Multihash.hash(it, input, 4u).bytes
                val decoded = Multihash.decode(encoded)
                assertTrue(decoded.check(input))
                assertFalse(decoded.check(wrong))
            }
        }
    }

    @Test
    fun throwsErrors() {
        // Decode invalid
        assertFailsWith(IllegalArgumentException::class) {
            Multihash.decode(byteArrayOf())
        }

        // Size zero
        assertFailsWith(IllegalArgumentException::class) {
            Multihash(MD5, 0u, byteArrayOf())
        }
        // Build with different size to digest.size
        assertFailsWith(IllegalArgumentException::class) {
            Multihash(MD5, 5u, byteArrayOf(0, 0, 0))
        }

        // Size zero
        assertFailsWith(IllegalArgumentException::class) {
            Multihash.hash(MD5, byteArrayOf(0), 0u)
        }
        // Too large size
        assertFailsWith(IllegalArgumentException::class) {
            Multihash.hash(MD5, byteArrayOf(0), 2000u)
        }

        // Try to get a non-multihash code
        assertFailsWith(IllegalArgumentException::class) {
            Multicodec.Companion.Multihash.get(6u)
        }

        // Trying to implement another overlapping hashing algorithm
        assertFailsWith(IllegalArgumentException::class) {
            Multicodec.Companion.Multihash(17u)
        }
    }
}