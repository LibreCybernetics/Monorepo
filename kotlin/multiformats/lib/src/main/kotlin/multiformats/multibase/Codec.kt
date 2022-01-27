package multiformats.multibase

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
        private fun base16Encode(bytes: ByteArray, alphabet: Alphabet) =
            bytes.map { it.toUByte() }.flatMap {
                listOf(
                    alphabet.char((it / 16.toUByte())),
                    alphabet.char(it.mod(16.toUByte()))
                )
            }.toCharArray().concatToString()

        @ExperimentalUnsignedTypes
        private fun base16Decode(encoded: String, alphabet: Alphabet) =
            encoded.map { alphabet.char(it) }.chunked(2).map {
                (it.component1() * 16.toUByte() + it.component2()).toUByte()
            }.toUByteArray().toByteArray()

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
                            if (ubyte.and(pow[7 - it]) > 0.toUByte()) '1' else '0'
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
                        val c0 = chunk[0].div(32.toUByte())
                        val c1 = chunk[0].rem(32.toUByte()).div(4.toUByte())
                        val c2 = chunk[0].rem(4.toUByte()) * 2.toUByte() + (chunk.elementAtOrNull(1)?.div(128.toUByte()) ?: 0.toUInt())
                        val c3 = if (chunk.size > 1) chunk[1].rem(128.toUByte()) / 16.toUByte() else null
                        val c4 = if (chunk.size > 1) chunk[1].rem(16.toUByte()) / 2.toUByte() else null
                        val c5 = if (chunk.size > 1) chunk[1].rem(2.toUByte()) * 4.toUByte() + (chunk.elementAtOrNull(2)?.div(64.toUByte()) ?: 0.toUInt()) else null
                        val c6 = if (chunk.size > 2) chunk[2].rem(64.toUByte()) / 8.toUByte() else null
                        val c7 = if (chunk.size > 2) chunk[2].rem(8.toUByte()) else null

                        arrayOf(c0, c1, c2, c3, c4, c5, c6, c7).filterNotNull().map {
                            alphabet.char(it)
                        }.toCharArray().concatToString()
                    }.reduceOrNull { a, b -> a + b } ?: ""

                override fun _decode(encoded: String): ByteArray =
                    encoded.chunked(8).flatMap { chunk ->
                        val b1 = alphabet.char(chunk[0]) * 32.toUByte() + alphabet.char(chunk[1]) * 4.toUByte() + alphabet.char(chunk[2]) / 2.toUByte()
                        val b2 = if (chunk.length > 3) {
                            alphabet.char(chunk[2]).rem(2.toUByte()) * 128.toUByte() + alphabet.char(chunk[3]) * 16.toUByte() + alphabet.char(chunk[4]) * 2.toUByte() + alphabet.char(chunk[5]) / 4.toUByte()
                        } else null
                        val b3 = if (chunk.length > 6) {
                            alphabet.char(chunk[5]).rem(128.toUByte()) * 64.toUByte() + alphabet.char(chunk[6]) * 8.toUByte() + alphabet.char(chunk[7])
                        } else null

                        listOfNotNull(b1, b2, b3).map { it.toUByte() }
                    }.toUByteArray().toByteArray()
            }

        @ExperimentalUnsignedTypes
        val Base16Lower =
            object : Codec('f') {
                override fun _encode(bytes: ByteArray): String = base16Encode(bytes, Alphabet.Companion.Base16Lower)
                override fun _decode(encoded: String): ByteArray = base16Decode(encoded, Alphabet.Companion.Base16Lower)
            }

        @ExperimentalUnsignedTypes
        val Base16Upper =
            object : Codec('F') {
                override fun _encode(bytes: ByteArray): String = base16Encode(bytes, Alphabet.Companion.Base16Upper)
                override fun _decode(encoded: String): ByteArray = base16Decode(encoded, Alphabet.Companion.Base16Upper)
            }

        // val Base32 = Codec('b')
        // val Base32Upper = Codec('B')
        // val Base58BTC = Codec('z')
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
