package multiformats.multibase

import java.math.BigInteger

abstract class Codec(val code: Char) {
    abstract fun _encode(bytes: ByteArray): String
    abstract fun _decode(encoded: String): ByteArray

    fun encode(bytes: ByteArray): String = "$code${_encode(bytes)}"
    fun decode(str: String): ByteArray {
        require(str.isNotEmpty())
        require(str.first() == code)

        return _decode(str.drop(1))
    }

    init {
        synchronized(Codec) {
            require(!registered.contains(code))
            registered.plusAssign(code to this)
        }
    }

    companion object {
        private val registered: MutableMap<Char, Codec> = mutableMapOf()

        @ExperimentalUnsignedTypes
        private fun base10(bytes: ByteArray): BigInteger =
            if (bytes.isEmpty()) BigInteger.ZERO else
                bytes.last().toUByte().toInt().toBigInteger() + 256.toBigInteger() * base10(bytes.dropLast(1).toByteArray())

        @ExperimentalUnsignedTypes
        private fun base10(int: BigInteger): UByteArray = if (int < 256.toBigInteger()) ubyteArrayOf(int.intValueExact().toUByte()) else {
            val d = int.div(256.toBigInteger())
            val r = int.rem(256.toBigInteger()).intValueExact().toUByte()
            base10(d) + r
        }

        @ExperimentalUnsignedTypes
        private fun base16(bytes: ByteArray, alphabet: Alphabet) =
            bytes.map { it.toUByte() }.flatMap {
                listOf(
                    alphabet.char((it / 16u)),
                    alphabet.char(it.mod(16u))
                )
            }.toCharArray().concatToString()

        @ExperimentalUnsignedTypes
        private fun base16(encoded: String, alphabet: Alphabet) =
            encoded.map { alphabet.char(it) }.chunked(2).map {
                (it.component1() * 16u + it.component2()).toUByte()
            }.toUByteArray().toByteArray()

        @ExperimentalUnsignedTypes
        private fun base32(bytes: ByteArray, alphabet: Alphabet, pad: Boolean): String =
            bytes.toUByteArray().chunked(5).map { chunk ->
                val c0 = chunk[0] / 8u
                val c1 = chunk[0].rem(8u) * 4u + (chunk.elementAtOrNull(1)?.div(64u) ?: 0u)
                val c2 = if (chunk.size > 1) chunk[1].rem(64u) / 2u else null
                val c3 = if (chunk.size > 1) chunk[1].rem(2u) * 16u + (chunk.elementAtOrNull(2)?.div(16u) ?: 0u) else null
                val c4 = if (chunk.size > 2) chunk[2].rem(16u) * 2u + (chunk.elementAtOrNull(3)?.div(128u) ?: 0u) else null
                val c5 = if (chunk.size > 3) chunk[3].rem(128u) / 4u else null
                val c6 = if (chunk.size > 3) chunk[3].rem(4u) * 8u + (chunk.elementAtOrNull(4)?.div(32u) ?: 0u) else null
                val c7 = if (chunk.size > 4) chunk[4].rem(32u) else null

                arrayOf(c0, c1, c2, c3, c4, c5, c6, c7).mapNotNull {
                    if (it != null) alphabet.char(it) else (if (pad) '=' else it)
                }.toCharArray().concatToString()
            }.reduceOrNull { a, b -> a + b } ?: ""

        @ExperimentalUnsignedTypes
        private fun base32(encoded: String, alphabet: Alphabet): ByteArray =
            encoded.dropLastWhile { it == '=' }.chunked(8).flatMap { chunk ->
                val b0 = alphabet.char(chunk[0]) * 8u + alphabet.char(chunk[1]) / 4u
                val b1 = if (chunk.length > 2) {
                    alphabet.char(chunk[1]).rem(4u) * 64u + alphabet.char(chunk[2]) * 2u + alphabet.char(chunk[3]) / 16u
                } else null
                val b2 = if (chunk.length > 4) {
                    alphabet.char(chunk[3]).rem(16u) * 16u + alphabet.char(chunk[4]) / 2u
                } else null
                val b3 = if (chunk.length > 5) {
                    alphabet.char(chunk[4]).rem(2u) * 128u + alphabet.char(chunk[5]) * 4u + alphabet.char(chunk[6]) / 8u
                } else null
                val b4 = if (chunk.length > 7) {
                    alphabet.char(chunk[6]).rem(8u) * 32u + alphabet.char(chunk[7])
                } else null

                listOfNotNull(b0, b1, b2, b3, b4).map { it.toUByte() }
            }.toUByteArray().toByteArray()

        private fun base58(int: BigInteger, alphabet: Alphabet): String =
            if (int < 58.toBigInteger()) alphabet.char(int.intValueExact()).toString() else
                base58(int / 58.toBigInteger(), alphabet) + alphabet.char(int.rem(58.toBigInteger()).intValueExact())

        private fun base58(encoded: String, alphabet: Alphabet): BigInteger =
            if (encoded.isEmpty()) BigInteger.ZERO else
                alphabet.char(encoded.last()).toUInt().toInt().toBigInteger() + 58.toBigInteger() * base58(encoded.dropLast(1), alphabet)

        val Identity =
            object : Codec(0.toChar()) {
                override fun _encode(bytes: ByteArray): String =
                    String(bytes, Charsets.ISO_8859_1)

                override fun _decode(encoded: String): ByteArray =
                    encoded.toByteArray(Charsets.ISO_8859_1)
            }

        @ExperimentalUnsignedTypes
        val Base2 =
            object : Codec('0') {
                private val pow: UByteArray = arrayOf(1, 2, 4, 8, 16, 32, 64, 128).map { it.toUByte() }.toUByteArray()

                override fun _encode(bytes: ByteArray): String =
                    bytes.toUByteArray().map { ubyte ->
                        (0..7).map {
                            if (ubyte.and(pow[7 - it]) > 0u) '1' else '0'
                        }.toCharArray().concatToString()
                    }.reduceOrNull { a, b -> a + b } ?: ""

                override fun _decode(encoded: String): ByteArray =
                    encoded.chunked(8).map {
                        it.mapIndexed { idx, char ->
                            (Alphabet.Companion.Base2.char(char) * pow[7 - idx]).toUByte()
                        }.reduce { a, b -> (a + b).toUByte() }
                    }.toUByteArray().toByteArray()
            }

        @ExperimentalUnsignedTypes
        val Base8 =
            object : Codec('7') {
                private val alphabet = Alphabet.Companion.Base8

                override fun _encode(bytes: ByteArray): String =
                    bytes.toUByteArray().chunked(3).map { chunk ->
                        val c0 = chunk[0] / 32u
                        val c1 = chunk[0].rem(32u) / 4u
                        val c2 = chunk[0].rem(4u) * 2u + (chunk.elementAtOrNull(1)?.div(128u) ?: 0u)
                        val c3 = if (chunk.size > 1) chunk[1].rem(128u) / 16u else null
                        val c4 = if (chunk.size > 1) chunk[1].rem(16u) / 2u else null
                        val c5 = if (chunk.size > 1) chunk[1].rem(2u) * 4u + (chunk.elementAtOrNull(2)?.div(64u) ?: 0u) else null
                        val c6 = if (chunk.size > 2) chunk[2].rem(64u) / 8u else null
                        val c7 = if (chunk.size > 2) chunk[2].rem(8u) else null

                        arrayOf(c0, c1, c2, c3, c4, c5, c6, c7).filterNotNull().map {
                            alphabet.char(it)
                        }.toCharArray().concatToString()
                    }.reduceOrNull { a, b -> a + b } ?: ""

                override fun _decode(encoded: String): ByteArray =
                    encoded.chunked(8).flatMap { chunk ->
                        val b1 = alphabet.char(chunk[0]) * 32u + alphabet.char(chunk[1]) * 4u + alphabet.char(chunk[2]) / 2u
                        val b2 = if (chunk.length > 3) {
                            alphabet.char(chunk[2]).rem(2u) * 128u + alphabet.char(chunk[3]) * 16u + alphabet.char(chunk[4]) * 2u + alphabet.char(chunk[5]) / 4u
                        } else null
                        val b3 = if (chunk.length > 6) {
                            alphabet.char(chunk[5]).rem(128u) * 64u + alphabet.char(chunk[6]) * 8u + alphabet.char(chunk[7])
                        } else null

                        listOfNotNull(b1, b2, b3).map { it.toUByte() }
                    }.toUByteArray().toByteArray()
            }

        @ExperimentalUnsignedTypes
        val Base10 =
            object : Codec('9') {
                override fun _encode(bytes: ByteArray): String {
                    val z = bytes.takeWhile { it == 0.toByte() }
                    val nz = bytes.dropWhile { it == 0.toByte() }
                    return z.map { '0' }.toCharArray().concatToString() + if (nz.isEmpty()) "" else base10(nz.toByteArray()).toString()
                }

                override fun _decode(encoded: String): ByteArray {
                    val z = encoded.takeWhile { it == '0' }
                    val nz = encoded.dropWhile { it == '0' }
                    return (z.map { 0.toUByte() }.toUByteArray() + if (nz.isEmpty()) ubyteArrayOf() else base10(nz.toBigInteger())).toByteArray()
                }
            }

        @ExperimentalUnsignedTypes
        val Base16Lower =
            object : Codec('f') {
                override fun _encode(bytes: ByteArray): String = base16(bytes, Alphabet.Companion.Base16Lower)
                override fun _decode(encoded: String): ByteArray = base16(encoded, Alphabet.Companion.Base16Lower)
            }

        @ExperimentalUnsignedTypes
        val Base16Upper =
            object : Codec('F') {
                override fun _encode(bytes: ByteArray): String = base16(bytes, Alphabet.Companion.Base16Upper)
                override fun _decode(encoded: String): ByteArray = base16(encoded, Alphabet.Companion.Base16Upper)
            }

        @ExperimentalUnsignedTypes
        val Base32Lower =
            object : Codec('b') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32Lower, false)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32Lower)
            }

        @ExperimentalUnsignedTypes
        val Base32Upper =
            object : Codec('B') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32Upper, false)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32Upper)
            }

        @ExperimentalUnsignedTypes
        val Base32LowerPad =
            object : Codec('c') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32Lower, true)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32Lower)
            }

        @ExperimentalUnsignedTypes
        val Base32UpperPad =
            object : Codec('C') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32Upper, true)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32Upper)
            }

        @ExperimentalUnsignedTypes
        val Base32HexLower =
            object : Codec('v') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32HexLower, false)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32HexLower)
            }

        @ExperimentalUnsignedTypes
        val Base32HexUpper =
            object : Codec('V') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32HexUpper, false)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32HexUpper)
            }

        @ExperimentalUnsignedTypes
        val Base32HexLowerPad =
            object : Codec('t') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32HexLower, true)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32HexLower)
            }

        @ExperimentalUnsignedTypes
        val Base32HexUpperPad =
            object : Codec('T') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32HexUpper, true)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32HexUpper)
            }

        @ExperimentalUnsignedTypes
        val ZBase32 =
            object : Codec('h') {
                override fun _encode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.ZBase32, false)
                override fun _decode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.ZBase32)
            }

        @ExperimentalUnsignedTypes
        val Base36Lower =
            object : Codec('k') {
                override fun _encode(bytes: ByteArray): String {
                    val z = bytes.takeWhile { it == 0.toByte() }
                    val nz = bytes.dropWhile { it == 0.toByte() }
                    return z.map { '0' }.toCharArray().concatToString() + if (nz.isEmpty()) "" else base10(nz.toByteArray()).toString(36)
                }

                override fun _decode(encoded: String): ByteArray {
                    val z = encoded.takeWhile { it == '0' }
                    val nz = encoded.dropWhile { it == '0' }
                    return (z.map { 0.toUByte() }.toUByteArray() + if (nz.isEmpty()) ubyteArrayOf() else base10(nz.toBigInteger(36))).toByteArray()
                }
            }

        @ExperimentalUnsignedTypes
        val Base36Upper =
            object : Codec('K') {
                override fun _encode(bytes: ByteArray): String {
                    val z = bytes.takeWhile { it == 0.toByte() }
                    val nz = bytes.dropWhile { it == 0.toByte() }
                    return z.map { '0' }.toCharArray().concatToString() + if (nz.isEmpty()) "" else base10(nz.toByteArray()).toString(36).map { it.uppercase() }.reduce { a, b -> a + b }
                }

                override fun _decode(encoded: String): ByteArray {
                    val z = encoded.takeWhile { it == '0' }
                    val nz = encoded.dropWhile { it == '0' }.map { it.lowercase() }.reduceOrNull { a, b -> a + b } ?: ""
                    return (z.map { 0.toUByte() }.toUByteArray() + if (nz.isEmpty()) ubyteArrayOf() else base10(nz.toBigInteger(36))).toByteArray()
                }
            }

        @ExperimentalUnsignedTypes
        val Base58 =
            object : Codec('z') {
                override fun _encode(bytes: ByteArray): String {
                    val z = bytes.takeWhile { it == 0.toByte() }
                    val nz = bytes.dropWhile { it == 0.toByte() }
                    return z.map { '1' }.toCharArray().concatToString() + if (nz.isEmpty()) "" else base58(base10(nz.toByteArray()), Alphabet.Companion.Base58)
                }

                override fun _decode(encoded: String): ByteArray {
                    val z = encoded.takeWhile { it == '1' }
                    val nz = encoded.dropWhile { it == '1' }
                    return (z.map { 0.toUByte() }.toUByteArray() + if (nz.isEmpty()) ubyteArrayOf() else base10(base58(nz, Alphabet.Companion.Base58))).toByteArray()
                }
            }

        @ExperimentalUnsignedTypes
        val Base58Flickr =
            object : Codec('Z') {
                override fun _encode(bytes: ByteArray): String {
                    val z = bytes.takeWhile { it == 0.toByte() }
                    val nz = bytes.dropWhile { it == 0.toByte() }
                    return z.map { '1' }.toCharArray().concatToString() + if (nz.isEmpty()) "" else base58(base10(nz.toByteArray()), Alphabet.Companion.Base58Flickr)
                }

                override fun _decode(encoded: String): ByteArray {
                    val z = encoded.takeWhile { it == '1' }
                    val nz = encoded.dropWhile { it == '1' }
                    return (z.map { 0.toUByte() }.toUByteArray() + if (nz.isEmpty()) ubyteArrayOf() else base10(base58(nz, Alphabet.Companion.Base58Flickr))).toByteArray()
                }
            }

        // val Base64 = Codec('m')
        // val Base64Pad = Codec('M')
        // val Base64URL = Codec('u')
        // val Base64URLPad = Codec('U')

        fun getCodec(code: Char): Codec = registered.getValue(code)

        fun decode(str: String): ByteArray {
            require(str.isNotEmpty())
            require(registered.contains(str.first()))

            return getCodec(str.first()).decode(str)
        }
    }
}
