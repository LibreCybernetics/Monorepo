package multiformats.multibase

import java.io.File
import kotlin.test.*

class MultibaseTest {
    @Test
    @ExperimentalUnsignedTypes
    fun exampleValuesFromSpec() {
        val content1: ByteArray = arrayOf(121, 101, 115, 32, 109, 97, 110, 105, 32, 33).map { it.toByte() }.toByteArray()
        val testVectors1: List<Pair<String, String>> =
            File("../../../spec/multiformats/multibase-basic.csv")
                .readLines()
                .map { it.split(',') }
                .map { Pair(it.component1().trim(), it.component2().trim()) }
                .filter { listOf("identity", "base16").contains(it.component1()) }

        testVectors1.forEach { (_, encoded) ->
            val codec = MultibaseCodec.getCodec(encoded.first())
            assertEquals(encoded, Multibase(content1, codec).encoded)
            assertContentEquals(content1, Multibase.decode(encoded))
        }
    }
}
