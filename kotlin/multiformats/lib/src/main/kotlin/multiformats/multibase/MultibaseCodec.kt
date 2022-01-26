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

        private val base2Alphabet: List<Char> = listOf('0', '1')
        private val base8Alphabet: List<Char> = listOf('0', '1', '2', '3', '4', '5', '6', '7')
        private val base16LowerAlphabet: CharArray = (base8Alphabet + listOf('8', '9', 'a', 'b', 'c', 'd', 'e', 'f')).toCharArray()
        private val base16UpperAlphabet: CharArray = (base8Alphabet + listOf('8', '9', 'A', 'B', 'C', 'D', 'E', 'F')).toCharArray()

        private fun base16Encode(bytes: ByteArray, alphabet: CharArray) =
            bytes.map { it.toUByte() }.flatMap {
                listOf(
                    alphabet.get((it / 16.toUByte()).toInt()),
                    alphabet.get(it.mod(16.toUByte()).toInt())
                )
            }.toCharArray().concatToString()

        private fun base16Decode(encoded: String, alphabet: CharArray) =
            encoded.map { alphabet.indexOf(it) }.chunked(2).map {
                (it.component1() * 16 + it.component2()).toUByte()
            }.toUByteArray().toByteArray()

        val Identity =
            object : MultibaseCodec(0.toChar()) {
                override fun _encode(bytes: ByteArray): String =
                    String(bytes, Charsets.ISO_8859_1)
                override fun _decode(encoded: String): ByteArray =
                    encoded.toByteArray(Charsets.ISO_8859_1)
            }

        @ExperimentalUnsignedTypes
        val Base16Lower =
            object : MultibaseCodec('f') {
                override fun _encode(bytes: ByteArray): String = base16Encode(bytes, base16LowerAlphabet)
                override fun _decode(encoded: String): ByteArray = base16Decode(encoded, base16LowerAlphabet)
            }

        @ExperimentalUnsignedTypes
        val Base16Upper =
            object : MultibaseCodec('F') {
                override fun _encode(bytes: ByteArray): String = base16Encode(bytes, base16UpperAlphabet)
                override fun _decode(encoded: String): ByteArray = base16Decode(encoded, base16UpperAlphabet)
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
