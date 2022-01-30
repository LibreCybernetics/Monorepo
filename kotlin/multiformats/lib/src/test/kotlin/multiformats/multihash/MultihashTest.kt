package multiformats.multihash

import multiformats.decoder
import kotlin.test.Test
import kotlin.random.Random
import java.io.File
import java.util.*
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

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

        testVectors.forEach { (input, multihash) ->
            val normal = Multihash.decode(multihash)
            val extraBytes = Multihash.decode(multihash + Random.nextBytes(10))
            assertEquals(normal.algorithm, extraBytes.algorithm)
            assertEquals(normal.size, extraBytes.size)
            assertContentEquals(normal.digest, extraBytes.digest)
        }
    }
}