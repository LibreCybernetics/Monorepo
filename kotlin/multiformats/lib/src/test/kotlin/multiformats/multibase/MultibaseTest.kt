package multiformats.multibase

import java.io.File
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import util.types.NonEmptyString

val decoder = java.util.Base64.getDecoder()

class MultibaseTest {
    @Test
    @ExperimentalUnsignedTypes
    fun exampleValuesFromSpec() {
        val testVectors: List<Pair<ByteArray, NonEmptyString>> =
            File("../../../spec/multiformats/multibase.csv")
                .readLines()
                .map { it.split(',') }
                .map { decoder.decode(it.component2().trim()) to NonEmptyString(it.component3().trim()) }

        testVectors.forEach { (bytes, encoded) ->
            val codec = Multibase.getCodec(encoded)
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

            Identity
            Base2
            Base8
            Base10
            Base16Lower
            Base16Upper
            Base32Lower
            Base32Upper
            Base32LowerPad
            Base32UpperPad
            Base32HexLower
            Base32HexUpper
            Base32HexLowerPad
            Base32HexUpperPad
            Base36Lower
            Base36Upper
            Base58
            Base58Flickr
            ZBase32
            Base64
            Base64Pad
            Base64URL
            Base64URLPad
        }
    }

    @Test
    @ExperimentalUnsignedTypes
    fun throwsErrors() {
        assertFailsWith(IllegalArgumentException::class) {
            Multibase.decode(NonEmptyString("3"))
        }
        assertFailsWith(IllegalArgumentException::class) {
            Identity.decode(NonEmptyString("3"))
        }
        assertFailsWith(IllegalArgumentException::class) {
            object : Codec('f') {
                override fun directEncode(bytes: ByteArray): String = ""
                override fun directDecode(encoded: String): ByteArray = byteArrayOf()
            }
        }
    }
}
