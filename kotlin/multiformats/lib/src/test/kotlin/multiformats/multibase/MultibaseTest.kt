package multiformats.multibase

import java.io.File
import kotlin.random.Random
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
                .filter { listOf("identity", "base2", "base10", "base8", "base16", "base16upper").contains(it.component1()) }

        testVectors1.forEach { (_, encoded) ->
            val codec = Codec.getCodec(encoded.first())
            assertEquals(encoded, Multibase(content1, codec).encoded)
            assertContentEquals(content1, Multibase(encoded).bytes)
        }
    }

    @Test
    @ExperimentalUnsignedTypes
    fun randomValues() {
        for (i in 1..1000) {
            val length = Random.nextInt(100)
            val bytes = Random.nextBytes(length)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Identity).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base2).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base8).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base10).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base16Lower).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base16Upper).encoded).bytes)
        }
    }

    @Test
    @ExperimentalUnsignedTypes
    fun throwsErrors() {
        assertFailsWith(IllegalArgumentException::class) {
            Multibase.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Identity.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base2.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base8.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base10.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base16Lower.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base16Upper.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Multibase.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Identity.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base2.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base8.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base10.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base16Lower.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Base16Upper.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            object : Codec('f') {
                override fun _encode(bytes: ByteArray): String = ""
                override fun _decode(encoded: String): ByteArray = byteArrayOf()
            }
        }
    }
}
