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
                .filter { !listOf("base64", "base64pad", "base64url", "base64urlpad").contains(it.component1()) }

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
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base32Lower).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base32Upper).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base32LowerPad).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base32UpperPad).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base32HexLower).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base32HexUpper).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base32HexLowerPad).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base32HexUpperPad).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base36Lower).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base36Upper).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base58).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.Base58Flickr).encoded).bytes)
            assertContentEquals(bytes, Multibase(Multibase(bytes, Codec.ZBase32).encoded).bytes)
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
            Multibase.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codec.Identity.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            object : Codec('f') {
                override fun _encode(bytes: ByteArray): String = ""
                override fun _decode(encoded: String): ByteArray = byteArrayOf()
            }
        }
    }
}
