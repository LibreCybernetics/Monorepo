package multiformats.multibase

abstract class MultibaseCodec(val code: Char) {
    abstract fun _encode(bytes: ByteArray): String
    abstract fun _decode(encoded: String): ByteArray

    fun encode(bytes: ByteArray): String = "$code${_encode(bytes)}"
    fun decode(str: String): ByteArray {
        require(str.isNotEmpty())
        require(str.first() == code)

        return _decode(str.drop(1))
    }

    init {
        synchronized(MultibaseCodec) {
            require(!registered.contains(code))
            registered.plusAssign(code to this)
        }
    }

    companion object {
        private val registered: MutableMap<Char, MultibaseCodec> = mutableMapOf()

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
            object : MultibaseCodec(0.toChar()) {
                override fun _encode(bytes: ByteArray): String =
                    String(bytes, Charsets.ISO_8859_1)

                override fun _decode(encoded: String): ByteArray =
                    encoded.toByteArray(Charsets.ISO_8859_1)
            }

        @ExperimentalUnsignedTypes
        val Base2 =
            object : MultibaseCodec('0') {
                private val pow: UByteArray = arrayOf(1, 2, 4, 8, 16, 32, 64, 128).map { it.toUByte() }.toUByteArray()

                private fun char(ubyte: UByte, place: Int): Char =
                    if (ubyte.and(pow.get(place)) > 0.toUByte()) '1' else '0'

                private fun char(char: Char, place: Int): UByte =
                    (Alphabet.Companion.Base2.char(char) * pow[place]).toUByte()

                override fun _encode(bytes: ByteArray): String =
                    bytes.toUByteArray()
                        .map { ubyte -> (0..7).map { char(ubyte, 7 - it) }.toCharArray().concatToString() }
                        .reduceOrNull { a, b -> a + b } ?: ""

                override fun _decode(encoded: String): ByteArray =
                    encoded.chunked(8)
                        .map { it.mapIndexed { idx, chr -> char(chr, 7 - idx) }.reduce { a, b -> (a + b).toUByte() } }
                        .toUByteArray().toByteArray()
            }

        @ExperimentalUnsignedTypes
        val Base16Lower =
            object : MultibaseCodec('f') {
                override fun _encode(bytes: ByteArray): String = base16Encode(bytes, Alphabet.Companion.Base16Lower)
                override fun _decode(encoded: String): ByteArray = base16Decode(encoded, Alphabet.Companion.Base16Lower)
            }

        @ExperimentalUnsignedTypes
        val Base16Upper =
            object : MultibaseCodec('F') {
                override fun _encode(bytes: ByteArray): String = base16Encode(bytes, Alphabet.Companion.Base16Upper)
                override fun _decode(encoded: String): ByteArray = base16Decode(encoded, Alphabet.Companion.Base16Upper)
            }

        // val Base32 = MultibaseCodec('b')
        // val Base32Upper = MultibaseCodec('B')
        // val Base58BTC = MultibaseCodec('z')
        // val Base64 = MultibaseCodec('m')
        // val Base64Pad = MultibaseCodec('M')
        // val Base64URL = MultibaseCodec('u')
        // val Base64URLPad = MultibaseCodec('U')

        fun getCodec(code: Char): MultibaseCodec = registered.getValue(code)

        fun decode(str: String): ByteArray {
            require(str.isNotEmpty())
            require(registered.contains(str.first()))

            return getCodec(str.first()).decode(str)
        }
    }
}
