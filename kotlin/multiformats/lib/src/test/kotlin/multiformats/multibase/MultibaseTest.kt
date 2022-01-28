package multiformats.multibase

import java.io.File
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

val decoder = java.util.Base64.getDecoder()

class MultibaseTest {
    @Test
    @ExperimentalUnsignedTypes
    fun exampleValuesFromSpec() {
        val testVectors: List<Pair<ByteArray, String>> =
            File("../../../spec/multiformats/multibase.csv")
                .readLines()
                .map { it.split(',') }
                .map { decoder.decode(it.component2().trim()) to it.component3().trim() }

        testVectors.forEach { (bytes, encoded) ->
            val codec = Codec.getCodec(encoded.first())
            assertEquals(encoded, Multibase(bytes, codec).encoded)
            assertContentEquals(bytes, Multibase(encoded).bytes)
        }
    }

    @Test
    @ExperimentalUnsignedTypes
    fun randomValues() {
        for (i in 1..1000) {
            val length = Random.nextInt(100)
            val bytes = Random.nextBytes(length)

            Codec.Companion.getCodecs().forEach { codec ->
                assertContentEquals(bytes, Multibase(Multibase(bytes, codec).encoded).bytes)
            }

            Codecs.Identity
            Codecs.Base2
            Codecs.Base8
            Codecs.Base10
            Codecs.Base16Lower
            Codecs.Base16Upper
            Codecs.Base32Lower
            Codecs.Base32Upper
            Codecs.Base32LowerPad
            Codecs.Base32UpperPad
            Codecs.Base32HexLower
            Codecs.Base32HexUpper
            Codecs.Base32HexLowerPad
            Codecs.Base32HexUpperPad
            Codecs.Base36Lower
            Codecs.Base36Upper
            Codecs.Base58
            Codecs.Base58Flickr
            Codecs.ZBase32
            Codecs.Base64
            Codecs.Base64Pad
            Codecs.Base64URL
            Codecs.Base64URLPad
        }
    }

    @Test
    @ExperimentalUnsignedTypes
    fun throwsErrors() {
        assertFailsWith(IllegalArgumentException::class) {
            Multibase.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codecs.Identity.decode("")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Multibase.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            Codecs.Identity.decode("3")
        }
        assertFailsWith(IllegalArgumentException::class) {
            object : Codec('f') {
                override fun directEncode(bytes: ByteArray): String = ""
                override fun directDecode(encoded: String): ByteArray = byteArrayOf()
            }
        }
    }
}
