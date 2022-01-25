package multiformats

abstract class Multibase(val code: Char) {
    abstract fun _encode(bytes: ByteArray): String
    abstract fun _decode(str: String): ByteArray

    fun encode(bytes: ByteArray): String = "$code${_encode(bytes)}"
    fun decode(str: String): ByteArray {
        require(str.isNotEmpty())
        require(str.first() == code)

        return _decode(str.drop(1))
    }

    init {
        synchronized(Multibase) {
            require(!registered.contains(code))
            registered.plusAssign(Pair(code, this))
        }
    }

    companion object {
        private val registered: MutableMap<Char, Multibase> = mutableMapOf()

        private val base16alphabet: CharArray = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f'
        )

        val Identity =
            object : Multibase(0.toChar()) {
                override fun _encode(bytes: ByteArray): String =
                    String(bytes, Charsets.US_ASCII)
                override fun _decode(str: String): ByteArray =
                    str.toByteArray(Charsets.US_ASCII)
            }

        @ExperimentalUnsignedTypes
        val Base16 =
            object : Multibase('f') {
                override fun _encode(bytes: ByteArray): String =
                    bytes.map { it.toUByte() }.flatMap {
                        listOf(
                            base16alphabet.get((it / 16.toUByte()).toInt()),
                            base16alphabet.get(it.mod(16.toUByte()).toInt())
                        )
                    }.toCharArray().concatToString()
                override fun _decode(str: String): ByteArray =
                    str.map { base16alphabet.indexOf(it) }.chunked(2).map {
                        (it.component1() * 16 + it.component2()).toUByte()
                    }.toUByteArray().toByteArray()
            }

        // val Base16Upper = Multibase('F')
        // val Base32 = Multibase('b')
        // val Base32Upper = Multibase('B')
        // val Base58BTC = Multibase('z')
        // val Base64 = Multibase('m')
        // val Base64Pad = Multibase('M')
        // val Base64URL = Multibase('u')
        // val Base64URLPad = Multibase('U')

		fun getCodec(code: Char): Multibase = registered.getValue(code)

        fun decode(str: String): ByteArray {
            require(str.isNotEmpty())
            require(registered.contains(str.first()))

            return registered.getValue(str.first()).decode(str)
        }
    }
}
